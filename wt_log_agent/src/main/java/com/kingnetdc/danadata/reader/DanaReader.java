package com.kingnetdc.danadata.reader;

import com.alibaba.fastjson.JSONObject;
import com.kingnetdc.danadata.sender.DanaServerSender;
import com.kingnetdc.danadata.sender.KafkaSender;
import com.kingnetdc.danadata.sender.Sender;
import com.kingnetdc.danadata.util.Common;
import com.kingnetdc.danadata.util.Md5Encrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Created by yangjun on 17/3/25.
 */
public class DanaReader extends Reader{
	private static final Logger logger = LoggerFactory.getLogger(DanaReader.class);

	//匹配规则
	private String fileMatchName = "";
	//目录
	private String fileDir = "";
	//当前读取文件名
	private String currentFileName;
	//文件读取位置
	private long offset;
	//目录下所有符合规则的文件列表
	private List<Path> fileList;
	//数据文件所在目录
	private Path sourcePath;
	//保存上次读取的信息的文件
	private String offset_file_name;
	//每次读取的最大行数
	private int maxReadNum = 1000;
	//true:表示当前文件可读，false：表示当前文件不存在，等待刷新文件列表后读取新的文件
	private boolean fileReading;

	private File curFile = null;
	private Sender sender;

	//是否是最后一个文件，当前读取文件是最后一个文件时，如果出现新文件需要在读一次当前文件，保证当前文件已经全部读完
	private boolean last_file = false;

	//上次打印时间
	private long printTime = System.currentTimeMillis();
	//每次打印距离间隔，单位为秒
	private int printWaite = 10;

	//打印文件列表更新时间
	private long printList = System.currentTimeMillis();
	//每次打印文件列表时间间隔，单位为秒
	private int printListWaite = 60;

	private boolean stopFlg = false;

	public boolean configure(String fileMatchName, String fileDir, int maxReadNum, Map<String, String> sendParams){
		this.fileMatchName = fileMatchName;
		this.fileDir = fileDir;
		this.maxReadNum = maxReadNum;
		this.sourcePath = Paths.get(this.fileDir);
		this.offset_file_name = "offset.txt";
		sender = new KafkaSender();
		sendParams.put("offsetFileName", this.offset_file_name);
		sender.configure(sendParams);
		if(!Files.isDirectory(this.sourcePath, new LinkOption[0])) {
			logger.error("Path: {} is not a directory, please check.", this.fileDir);
			return false;
		}
		updateFileList();

		//读取上次缓存的文件列表和位置
		File file = new File("./config/" + this.offset_file_name);
		BufferedReader bufReader = null;
		try {
			if(file.exists()) {
				bufReader = new BufferedReader(new FileReader(file));
				String s = bufReader.readLine();
				if (s != null && !s.trim().isEmpty()) {
					JSONObject json = JSONObject.parseObject(s);
					this.currentFileName = json.getString("fileName");
					this.offset = Integer.valueOf(json.getString("offset"));
					logger.info("----------------------------- {}", currentFileName);
					logger.info("----------------------------- {}", offset);
				}
			}
		} catch (IOException ioe) {
			logger.error("读取缓存offset文件失败。", ioe);
		} catch (Exception e) {
			logger.error("读取缓存offset异常。", e);
		} finally {
			if(bufReader != null) {
				try {
					bufReader.close();
				} catch (IOException e) {
					logger.error("关闭bufferReader异常。", e);
				}
			}
		}
		//设置当前读取文件
		setReadingFile();
		return true;
	}

	@Override
	public void run(){
		BufferedReader br = null;
		int line = 0;//当前文件读取到的第几行
		while(!stopFlg) {
			try {
				findReadFile();
				if(this.curFile == null) {
					//创建当前文件对象
					setCurFile();
				}
				//读文件之前设置当前读取文件是否是最后一个文件，如果是则在读完后更新文件列表时当前文件不是最后一个则还要再读一次
				isLastFile();
				if(br == null) {
					br = new BufferedReader(new FileReader(this.curFile));
					line = 0;
				}
				String str = null;
				List<String> msgList = new ArrayList<String>();

				while((str = br.readLine()) != null && !stopFlg) {
					line ++;
					if(line <= offset) {
						continue;
					} else {
						offset ++;
					}
					msgList.add(str);
					if(msgList.size() >= this.maxReadNum) {
						Map<String, String> offsetInfo = new HashMap<String, String>();
						offsetInfo.put(Common.file_name_key, this.currentFileName);
						offsetInfo.put(Common.offset_key, String.valueOf(this.offset));
						sender.send(msgList, offsetInfo);
						msgList.clear();//发送完毕清除
					}
					if(this.printTime <= System.currentTimeMillis() - this.printWaite*1000) {
						this.printTime = System.currentTimeMillis();
						logger.info("当前读取的文件：{},读到的行数：{}", this.currentFileName, this.offset);
					}
				}
				//最后读取的数据发送掉
				if(msgList.size() > 0) {
					Map<String, String> Info = new HashMap<String, String>();
					Info.put(Common.file_name_key, this.currentFileName);
					Info.put(Common.offset_key, String.valueOf(this.offset));
					sender.send(msgList, Info);
					msgList.clear();//发送完毕清除
				}
				if(!this.last_file) {
					//读之前该文件不是最后一个文件，当前文件已经读完，关闭
					br.close();
					br = null;
					line = 0;
				}
				if(this.printTime <= System.currentTimeMillis() - this.printWaite*1000) {
					this.printTime = System.currentTimeMillis();
					logger.info("当前读取的文件：{},读到的行数：{}", this.currentFileName, this.offset);
				}
				//更新文件列表
				updateReadingFile();
				if(fileList.size() > 0) {
					if(this.printList <= System.currentTimeMillis() - this.printListWaite*1000) {
						this.printList = System.currentTimeMillis();
						logger.info("文件列表更新成功，文件个数：{}，第一个文件名：{}， 最后一个文件名：{}", this.fileList.size(), this.fileList.get(0).getFileName(), this.fileList.get(this.fileList.size() - 1).getFileName());
					}
				} else {
					logger.info("文件列表更新成功，文件列表为空");
				}
				Thread.sleep(1000);
			} catch (Throwable e) {
				logger.error("读取文件异常。", e);
				updateReadingFile();
				setReadingFile();
			}
		}
	}

	//找到当前可以读的文件，如果当前没有文件可以读，则一直循环找文件
	private boolean findReadFile(){
		while (!stopFlg) {
			try {
				if (!this.fileReading) {
					updateFileList();
					//当前没有可读文件,设置可读文件
					if (setReadingFile()) {
						logger.info("开始读取文件：{}，初始offset：{}", this.currentFileName, this.offset);
						break;
					} else {
						Thread.sleep(1000);
					}
				} else {
					break;
				}
			} catch (Throwable e) {
				logger.error("初始化读取文件异常。", e);
			}
		}
		return true;
	}

	public boolean setStop(){
		this.stopFlg = true;
		return true;
	}

	//更新当前正在读的文件，检查是否需要跳到下一个文件
	private boolean updateReadingFile(){
		updateFileList();
		int index = getFileIndexInFileList(this.currentFileName);
		if(index >= 0) {
			if(index < this.fileList.size() -1) {
				if(this.last_file) {
					//上一次是最后一个文件，需要再读一次
					this.last_file = false;
					return true;
				}
				if(this.currentFileName != null && !this.currentFileName.isEmpty()) {
					logger.info("当前文件已经读取完毕，继续读取下一个文件，当前文件名：{}, 文件行数：{}", this.currentFileName, this.offset);
				}
				//获取下一个文件
				this.currentFileName = this.fileList.get(index + 1).getFileName().toString();
				//从文件开始位置读取
				this.offset = 0;
				//生成新文件对象
				setCurFile();
			}
		} else {
			logger.warn("当前文件不在列表中，请检测文件是否被删除了");
		}
		return true;
	}

	private void setCurFile(){
		this.curFile = new File(this.fileDir + "/" + this.currentFileName);
	}

	//更新文件列表
	private boolean updateFileList(){
		this.fileList = new ArrayList<Path>();
		DirectoryStream<Path> localDirectoryStream = null;
		try{
			localDirectoryStream = Files.newDirectoryStream(this.sourcePath, this.fileMatchName);
			for(Path localPath : localDirectoryStream) {
				this.fileList.add(localPath);
			}
			Collections.sort(this.fileList);
		} catch (IOException ioe) {
			logger.error("update file list failed: {}, with exception {}", this.sourcePath.toString(), ioe);
			return false;
		} finally {
			if(localDirectoryStream != null) {
				try {
					localDirectoryStream.close();
				} catch (IOException e) {
					logger.error("Close directory stream failed. ", e);
				}
			}
		}

		return true;
	}

	//检查文件名是否在文件列表中
	private boolean checkFileInList(String fileName) {
		if(fileName == null || fileName.isEmpty()) {
			logger.warn("文件名为空！");
			return false;
		}
		if(this.fileList != null && this.fileList.size() > 0) {
			for(Path p : this.fileList) {
				if(fileName.equals(p.getFileName().toString())) {
					return true;
				}
			}
		} else {
			logger.warn("文件列表为空");
		}
		return false;
	}

	//设置当前可读文件，返回false表示当前没有可读文件
	private boolean setReadingFile(){
		if(this.fileList == null || this.fileList.size() == 0 ) {
			logger.warn("文件目录下匹配到的文件列表为空");
			this.fileReading = false;
			return false;
		}
		if(this.currentFileName != null && !this.currentFileName.isEmpty()) {
			if(!checkFileInList(this.currentFileName)) {
				logger.warn("上次读取文件名在列表中不存在");
				if(this.currentFileName.compareTo(fileList.get(0).getFileName().toString()) < 0) {
					//上次读取文件在文件列表第一个文件之前，本次从文件列表第一个文件开始读取
					this.currentFileName = fileList.get(0).getFileName().toString();
					this.offset = 0;
					this.fileReading = true;
				} else if(this.currentFileName.compareTo(fileList.get(fileList.size()-1).getFileName().toString()) > 0) {
					logger.warn("列表中最后一个文件在上次读取文件之前，当前没有可以读取的文件。上次读取文件：{}，列表中最后一个文件：{}", this.currentFileName, fileList.get(fileList.size() - 1).getFileName().toString());
					this.fileReading = false;
					return false;
				} else if(this.currentFileName.compareTo(fileList.get(fileList.size()-1).getFileName().toString()) < 0) {
					logger.warn("列表中最后一个文件在上次读取文件之后，需要找到上次文件后的第一个文件。上次读取文件：{}，列表中最后一个文件：{}", this.currentFileName, fileList.get(fileList.size() - 1).getFileName().toString());
					for(Path p : this.fileList) {
						if(this.currentFileName.compareTo(p.getFileName().toString()) < 0) {
							this.currentFileName = p.getFileName().toString();
							this.offset = 0;
							this.fileReading = true;
							break;
						}
					}
				} else {
					logger.info("上次读取文件在列表中，本次读取从该文件开始： 文件名称：{} 开始的offset：{}", this.currentFileName, this.offset);
					this.fileReading = true;
				}
			} else {
				this.fileReading = true;
			}
		} else {
			logger.info("当前读取文件名为空，直接从第一个文件开始读。");
			this.currentFileName = this.fileList.get(0).getFileName().toString();
			this.offset = 0;
			this.fileReading = true;
		}
		return true;
	}

	//获取给定文件名在文件列表中的位置，返回-1表示文件名不在列表中，或列表为空
	private int getFileIndexInFileList(String fileName){
		int idx = -1;
		if(this.fileList != null && this.fileList.size() > 0 && fileName != null && !fileName.isEmpty()) {
			int i=0;
			for(Path p : this.fileList) {
				if(fileName.equals(p.getFileName().toString())) {
					idx = i;
					break;
				}
				i++;
			}
		}
		return idx;
	}

	//判断当前文件是否为列表中最后一个文件
	private boolean isLastFile(){
		if(this.fileList == null || this.fileList.size() == 0 || this.currentFileName == null || this.currentFileName.isEmpty()) {
			this.last_file = false;
			return false;
		}

		if(this.currentFileName.equals(this.fileList.get(this.fileList.size() - 1).getFileName().toString())) {
			this.last_file = true;
		} else {
			this.last_file = false;
		}
		return this.last_file;
	}

}
