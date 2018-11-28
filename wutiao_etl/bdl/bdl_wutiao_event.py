# coding: utf-8

#**********************程序说明*********************************#
#*模块: BDL
#*功能: 区块链bdl表
#*作者:gant
#*时间:2018-05-10
#*备注:区块链bdl表
#***************************************************************#

import kingnetdc

sql_day = '''
set parquet.compression=SNAPPY;
set mapreduce.map.memory.mb=20000;
set mapreduce.reduce.memory.mb=20000;
set hive.merge.mapredfiles=false;
set hive.merge.mapfiles=false;

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
,regexp_replace(msg,'\\n|\\r','\\t') as msg                
,type               
,regexp_replace(trace,'\\n|\\r','\\t') as trace              
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
,regexp_replace(keyword,'\\n|\\r','\\t') as keyword            
,upper(trim(pos)) as pos                
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
,if(event='hongbao' and rmb='1000',10,rmb) as rmb
,\`_sdk\`      
,\`_sdkver\`   
,gps_province
,gps_area
,order_id
,lower(trim(\`browser\`))  
,lower(trim(\`terminal\`))
,ad
,gameid
,adid
,eventid
,regexp_replace(lower(trim(\`device_id\`)),'\\n|\\r','\\t') as device_id
,kafkauniqueid
,'' as market_channel_type
,eventtype
,ds    
from {sourcetab} 
where ds = '{ds}' and event<>'withdraw' and eventid is null;

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
,regexp_replace(msg,'\\n|\\r','\\t') as msg                
,type               
,regexp_replace(trace,'\\n|\\r','\\t') as trace              
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
,regexp_replace(keyword,'\\n|\\r','\\t') as keyword            
,upper(trim(pos)) as pos                
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
,lower(trim(\`browser\`))  
,lower(trim(\`terminal\`))
,ad
,gameid
,adid
,eventid
,regexp_replace(lower(trim(\`device_id\`)),'\\n|\\r','\\t') as device_id
,kafkauniqueid
,'' as market_channel_type
,eventtype
,ds    
from(select 
t1.*, row_number() over (distribute by event,eventid sort by substr(\`_sst\`,1,10)) as rn
from {sourcetab} t1
where ds='{ds}' and event<>'withdraw' and eventid is not null) t2
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
,regexp_replace(msg,'\\n|\\r','\\t') as msg                
,type               
,regexp_replace(trace,'\\n|\\r','\\t') as trace              
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
,regexp_replace(keyword,'\\n|\\r','\\t') as keyword            
,upper(trim(pos)) as pos                
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
,lower(trim(\`browser\`))  
,lower(trim(\`terminal\`))
,ad
,gameid
,adid
,eventid
,regexp_replace(lower(trim(\`device_id\`)),'\\n|\\r','\\t') as device_id
,kafkauniqueid
,'' as market_channel_type
,eventtype
,ds    
from (select 
t1.*, row_number() over (distribute by order_id,status,type sort by substr(\`_sst\`,1,10)) as rn
from {sourcetab} t1
where ds='{ds}' and event='withdraw') t2
where rn=1;

insert overwrite table {targettab} partition(eventtype,ds)
select
 a.ouid
,a.ts
,a.serversystime
,a.did
,a.event
,a.project
,a.ip
,a.appver
,a.os
,a.osver
,a.model
,a.mfr
,a.res
,a.nettype
,a.carrier
,a.channel
,a.isvisitor
,a.step
,a.phone
,a.reftype
,regexp_replace(a.msg,'\\n|\\r','\\t') as msg
,a.type
,regexp_replace(a.trace,'\\n|\\r','\\t') as trace
,a.staytime
,a.commentid
,a.targetouid
,a.mediaid
,a.itemid
,a.itemtype
,a.itemlist
,a.sortid
,a.status
,a.shareplat
,a.replyid
,a.target
,a.timemachid
,a.isvalid
,a.subjectid
,regexp_replace(a.keyword,'\\n|\\r','\\t') as keyword
,a.pos
,a.userid
,a.inviterid
,a.familyid
,a.contributionpoint
,a.verifypoint
,a.incref
,a.accemeter
,a.magfield
,a.orient
,a.gyros
,a.light
,a.press
,a.tempera
,a.prox
,a.grav
,a.lineacce
,a.rota
,a.gps
,a.sort
,a.usertype
,a.coin
,a.rate
,a.rmb
,a.sdk
,a.sdkver
,a.gps_province
,a.gps_area
,a.order_id
,a.browser
,a.terminal
,a.ad
,a.gameid
,a.adid
,a.eventid
,regexp_replace(a.device_id,'\\n|\\r','\\t') as device_id
,a.kafkauniqueid
,b.media_type as market_channel_type
,a.eventtype
,a.ds
from (select *
from {targettab}
where ds='{ds}') as a 
left join {sourcetab2} as b
on a.channel=b.id;
'''


def main():
    kdc = kingnetdc.kdc
    ds = kdc.workDate
    sql = sql_day.format(ds=ds, targettab='wutiao.bdl_wutiao_event', sourcetab='wutiao.odl_event_qkl', sourcetab2='wutiao.odl_wutiao_sdk_channel_sync')
    kdc.debug = True
    print(sql)
    kdc.doHive(sql)


if __name__ == '__main__':
    main()
