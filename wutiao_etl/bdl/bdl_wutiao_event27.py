# coding: utf-8

#**********************程序说明*********************************#
#*模块: BDL
#*功能: 区块链bdl表
#*作者:gant
#*时间:2018-05-10
#*备注:区块链bdl表
#***************************************************************#

import kingnet

sql_day = '''
set parquet.compression=SNAPPY;

insert overwrite table {targettab} partition (eventtype,ds)
select
ouid                
,substr(\`_sst\`,1,10) as ts          
,\`timestamp\` as serversystime        
,lower(trim(did)) as did                
,event              
,project            
,\`_ip\`            
,lower(trim(\`_appver\`))        
,lower(trim(\`_os\`))           
,lower(trim(\`_osver\`))        
,lower(trim(\`_model\`))        
,lower(trim(\`_mfr\`))         
,lower(trim(\`_res\`))           
,lower(trim(\`_nettype\`))       
,lower(trim(\`_carrier\`))       
,lower(trim(\`_channel\`))       
,isvisitor          
,step               
,phone              
,reftype            
,msg                
,type               
,trace              
,staytime           
,commentid          
,targetouid         
,mediaid            
,itemid             
,itemtype           
,itemlist           
,sortid             
,status             
,shareplat          
,replyid            
,target           
,timemachid         
,isvalid            
,subjectid          
,keyword            
,pos                
,userid             
,inviterid          
,familyid            
,contributionpoint  
,verifypoint        
,incref             
,\`_accemeter\`     
,\`_magfield\`      
,\`_orient\`        
,\`_gyros\`         
,\`_light\`         
,\`_press\`         
,\`_tempera\`       
,\`_prox\`          
,\`_grav\`          
,\`_lineacce\`      
,\`_rota\`          
,\`_gps\` 
,sort
,usertype
,coin
,rate
,rmb
,\`_sdk\`      
,\`_sdkver\`   
,gps_province
,gps_area
,order_id
,null as eventtype
,ds    
from {sourcetab} 
where ds = '{ds}' and event<>'withdraw';;

insert into table {targettab} partition (eventtype,ds)
select
ouid                
,substr(\`_sst\`,1,10) as ts          
,\`timestamp\` as serversystime        
,lower(trim(did)) as did                
,event              
,project            
,\`_ip\`            
,lower(trim(\`_appver\`))        
,lower(trim(\`_os\`))           
,lower(trim(\`_osver\`))        
,lower(trim(\`_model\`))        
,lower(trim(\`_mfr\`))         
,lower(trim(\`_res\`))           
,lower(trim(\`_nettype\`))       
,lower(trim(\`_carrier\`))       
,lower(trim(\`_channel\`))       
,isvisitor          
,step               
,phone              
,reftype            
,msg                
,type               
,trace              
,staytime           
,commentid          
,targetouid         
,mediaid            
,itemid             
,itemtype           
,itemlist           
,sortid             
,status             
,shareplat          
,replyid            
,target           
,timemachid         
,isvalid            
,subjectid          
,keyword            
,pos                
,userid             
,inviterid          
,familyid            
,contributionpoint  
,verifypoint        
,incref             
,\`_accemeter\`     
,\`_magfield\`      
,\`_orient\`        
,\`_gyros\`         
,\`_light\`         
,\`_press\`         
,\`_tempera\`       
,\`_prox\`          
,\`_grav\`          
,\`_lineacce\`      
,\`_rota\`          
,\`_gps\` 
,sort
,usertype
,coin
,rate
,rmb
,\`_sdk\`      
,\`_sdkver\`   
,gps_province
,gps_area
,order_id
,null as eventtype
,ds    
from (select 
t1.*, row_number() over (distribute by order_id,status sort by substr(\`_sst\`,1,10)) as rn
from {sourcetab} t1
where ds='{ds}' and event='withdraw' and order_id is not null) t2
where rn=1;

insert into table {targettab} partition (eventtype,ds)
select
ouid                
,substr(\`_sst\`,1,10) as ts          
,\`timestamp\` as serversystime        
,lower(trim(did)) as did                
,event              
,project            
,\`_ip\`            
,lower(trim(\`_appver\`))        
,lower(trim(\`_os\`))           
,lower(trim(\`_osver\`))        
,lower(trim(\`_model\`))        
,lower(trim(\`_mfr\`))         
,lower(trim(\`_res\`))           
,lower(trim(\`_nettype\`))       
,lower(trim(\`_carrier\`))       
,lower(trim(\`_channel\`))       
,isvisitor          
,step               
,phone              
,reftype            
,msg                
,type               
,trace              
,staytime           
,commentid          
,targetouid         
,mediaid            
,itemid             
,itemtype           
,itemlist           
,sortid             
,status             
,shareplat          
,replyid            
,target           
,timemachid         
,isvalid            
,subjectid          
,keyword            
,pos                
,userid             
,inviterid          
,familyid            
,contributionpoint  
,verifypoint        
,incref             
,\`_accemeter\`     
,\`_magfield\`      
,\`_orient\`        
,\`_gyros\`         
,\`_light\`         
,\`_press\`         
,\`_tempera\`       
,\`_prox\`          
,\`_grav\`          
,\`_lineacce\`      
,\`_rota\`          
,\`_gps\` 
,sort
,usertype
,coin
,rate
,rmb
,\`_sdk\`      
,\`_sdkver\`   
,gps_province
,gps_area
,order_id
,null as eventtype
,ds    
from {sourcetab} t1
where ds='{ds}' and event='withdraw' and order_id is null;
'''

def main():
    kdc = kingnet.kdc()
    ds = kdc.workDate
    sql = sql_day.format(ds=ds, targettab='wutiao.bdl_wutiao_event', sourcetab='wutiao.odl_event_qkl')
    kdc.debug = True
    print(sql)
    kdc.doHive(sql)

if __name__ == '__main__':
    main()
