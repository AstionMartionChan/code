DROP TABLE IF EXISTS `t_kafka2hive_relation_config`;
CREATE TABLE `t_kafka2hive_relation_config`  (
  `id` bigint(10) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `topic` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'kafka topic',
  `hive_table` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'hive table',
  `relation_json` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'hive 表结构与 json数据的映射关系',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

INSERT INTO `t_kafka2hive_relation_config` VALUES (4, 'wutiao_super', 'wutiao.odl_event_qkl_testnew', '{\"relation\":[{\"jsonPath\":\"ouid\",\"hiveField\":\"ouid\",\"hiveType\":\"string\"},{\"jsonPath\":\"timestamp\",\"hiveField\":\"timestamp\",\"hiveType\":\"long\"},{\"jsonPath\":\"properties._sst\",\"hiveField\":\"_sst\",\"hiveType\":\"long\"},{\"jsonPath\":\"did\",\"hiveField\":\"did\",\"hiveType\":\"string\"},{\"jsonPath\":\"event\",\"hiveField\":\"event\",\"hiveType\":\"string\"},{\"jsonPath\":\"project\",\"hiveField\":\"project\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._ip\",\"hiveField\":\"_ip\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._appver\",\"hiveField\":\"_appver\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._os\",\"hiveField\":\"_os\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._osver\",\"hiveField\":\"_osver\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._model\",\"hiveField\":\"_model\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._mfr\",\"hiveField\":\"_mfr\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._res\",\"hiveField\":\"_res\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._nettype\",\"hiveField\":\"_nettype\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._carrier\",\"hiveField\":\"_carrier\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._channel\",\"hiveField\":\"_channel\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.isvisitor\",\"hiveField\":\"isvisitor\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.step\",\"hiveField\":\"step\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.phone\",\"hiveField\":\"phone\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.reftype\",\"hiveField\":\"reftype\",\"hiveType\":\"long\"},{\"jsonPath\":\"properties.msg\",\"hiveField\":\"msg\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.type\",\"hiveField\":\"type\",\"hiveType\":\"long\"},{\"jsonPath\":\"properties.trace\",\"hiveField\":\"trace\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.staytime\",\"hiveField\":\"staytime\",\"hiveType\":\"long\"},{\"jsonPath\":\"properties.commentid\",\"hiveField\":\"commentid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.targetouid\",\"hiveField\":\"targetouid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.mediaid\",\"hiveField\":\"mediaid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.itemid\",\"hiveField\":\"itemid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.itemtype\",\"hiveField\":\"itemtype\",\"hiveType\":\"long\"},{\"jsonPath\":\"properties.itemlist\",\"hiveField\":\"itemlist\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.sortid\",\"hiveField\":\"sortid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.status\",\"hiveField\":\"status\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.shareplat\",\"hiveField\":\"shareplat\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.replyid\",\"hiveField\":\"replyid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.target\",\"hiveField\":\"target\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.timemachid\",\"hiveField\":\"timemachid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.isvalid\",\"hiveField\":\"isvalid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.subjectid\",\"hiveField\":\"subjectid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.keyword\",\"hiveField\":\"keyword\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.pos\",\"hiveField\":\"pos\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.userid\",\"hiveField\":\"userid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.inviterid\",\"hiveField\":\"inviterid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.familyid\",\"hiveField\":\"familyid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.contributionpoint\",\"hiveField\":\"contributionpoint\",\"hiveType\":\"double\"},{\"jsonPath\":\"properties.verifypoint\",\"hiveField\":\"verifypoint\",\"hiveType\":\"double\"},{\"jsonPath\":\"properties.incref\",\"hiveField\":\"incref\",\"hiveType\":\"long\"},{\"jsonPath\":\"properties._accemeter\",\"hiveField\":\"_accemeter\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._magfield\",\"hiveField\":\"_magfield\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._orient\",\"hiveField\":\"_orient\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._gyros\",\"hiveField\":\"_gyros\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._light\",\"hiveField\":\"_light\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._press\",\"hiveField\":\"_press\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._tempera\",\"hiveField\":\"_tempera\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._prox\",\"hiveField\":\"_prox\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._grav\",\"hiveField\":\"_grav\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._lineacce\",\"hiveField\":\"_lineacce\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._rota\",\"hiveField\":\"_rota\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._gps\",\"hiveField\":\"_gps\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.sort\",\"hiveField\":\"sort\",\"hiveType\":\"int\"},{\"jsonPath\":\"properties.usertype\",\"hiveField\":\"usertype\",\"hiveType\":\"int\"},{\"jsonPath\":\"properties.coin\",\"hiveField\":\"coin\",\"hiveType\":\"double\"},{\"jsonPath\":\"properties.rate\",\"hiveField\":\"rate\",\"hiveType\":\"double\"},{\"jsonPath\":\"properties.rmb\",\"hiveField\":\"rmb\",\"hiveType\":\"double\"},{\"jsonPath\":\"properties._sdk\",\"hiveField\":\"_sdk\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._sdkver\",\"hiveField\":\"_sdkver\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.gps_province\",\"hiveField\":\"gps_province\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.gps_area\",\"hiveField\":\"gps_area\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.order_id\",\"hiveField\":\"order_id\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.browser\",\"hiveField\":\"browser\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.terminal\",\"hiveField\":\"terminal\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.ad\",\"hiveField\":\"ad\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.gameid\",\"hiveField\":\"gameid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.adid\",\"hiveField\":\"adid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.eventid\",\"hiveField\":\"eventid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.device_id\",\"hiveField\":\"device_id\",\"hiveType\":\"string\"},{\"jsonPath\":\"kafka_unique_id\",\"hiveField\":\"kafkauniqueid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.templateid\",\"hiveField\":\"templateid\",\"hiveType\":\"string\"}],\"partition\":[{\"name\":\"eventtype\",\"expression\":\"\'super\' as eventtype\"},{\"name\":\"ds\",\"expression\":\"from_unixtime(unix_timestamp(),\'yyyy-MM-dd\') as ds\"},{\"name\":\"hour\",\"expression\":\"from_unixtime(unix_timestamp(),\'yyyyMMddHH\') as hour\"}],\"where\":\"\"}\r\n');
INSERT INTO `t_kafka2hive_relation_config` VALUES (5, 'wutiao_high', 'wutiao.odl_event_qkl_testnew', '{\"relation\":[{\"jsonPath\":\"ouid\",\"hiveField\":\"ouid\",\"hiveType\":\"string\"},{\"jsonPath\":\"timestamp\",\"hiveField\":\"timestamp\",\"hiveType\":\"long\"},{\"jsonPath\":\"properties._sst\",\"hiveField\":\"_sst\",\"hiveType\":\"long\"},{\"jsonPath\":\"did\",\"hiveField\":\"did\",\"hiveType\":\"string\"},{\"jsonPath\":\"event\",\"hiveField\":\"event\",\"hiveType\":\"string\"},{\"jsonPath\":\"project\",\"hiveField\":\"project\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._ip\",\"hiveField\":\"_ip\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._appver\",\"hiveField\":\"_appver\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._os\",\"hiveField\":\"_os\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._osver\",\"hiveField\":\"_osver\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._model\",\"hiveField\":\"_model\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._mfr\",\"hiveField\":\"_mfr\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._res\",\"hiveField\":\"_res\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._nettype\",\"hiveField\":\"_nettype\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._carrier\",\"hiveField\":\"_carrier\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._channel\",\"hiveField\":\"_channel\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.isvisitor\",\"hiveField\":\"isvisitor\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.step\",\"hiveField\":\"step\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.phone\",\"hiveField\":\"phone\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.reftype\",\"hiveField\":\"reftype\",\"hiveType\":\"long\"},{\"jsonPath\":\"properties.msg\",\"hiveField\":\"msg\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.type\",\"hiveField\":\"type\",\"hiveType\":\"long\"},{\"jsonPath\":\"properties.trace\",\"hiveField\":\"trace\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.staytime\",\"hiveField\":\"staytime\",\"hiveType\":\"long\"},{\"jsonPath\":\"properties.commentid\",\"hiveField\":\"commentid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.targetouid\",\"hiveField\":\"targetouid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.mediaid\",\"hiveField\":\"mediaid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.itemid\",\"hiveField\":\"itemid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.itemtype\",\"hiveField\":\"itemtype\",\"hiveType\":\"long\"},{\"jsonPath\":\"properties.itemlist\",\"hiveField\":\"itemlist\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.sortid\",\"hiveField\":\"sortid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.status\",\"hiveField\":\"status\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.shareplat\",\"hiveField\":\"shareplat\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.replyid\",\"hiveField\":\"replyid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.target\",\"hiveField\":\"target\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.timemachid\",\"hiveField\":\"timemachid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.isvalid\",\"hiveField\":\"isvalid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.subjectid\",\"hiveField\":\"subjectid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.keyword\",\"hiveField\":\"keyword\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.pos\",\"hiveField\":\"pos\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.userid\",\"hiveField\":\"userid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.inviterid\",\"hiveField\":\"inviterid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.familyid\",\"hiveField\":\"familyid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.contributionpoint\",\"hiveField\":\"contributionpoint\",\"hiveType\":\"double\"},{\"jsonPath\":\"properties.verifypoint\",\"hiveField\":\"verifypoint\",\"hiveType\":\"double\"},{\"jsonPath\":\"properties.incref\",\"hiveField\":\"incref\",\"hiveType\":\"long\"},{\"jsonPath\":\"properties._accemeter\",\"hiveField\":\"_accemeter\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._magfield\",\"hiveField\":\"_magfield\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._orient\",\"hiveField\":\"_orient\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._gyros\",\"hiveField\":\"_gyros\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._light\",\"hiveField\":\"_light\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._press\",\"hiveField\":\"_press\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._tempera\",\"hiveField\":\"_tempera\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._prox\",\"hiveField\":\"_prox\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._grav\",\"hiveField\":\"_grav\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._lineacce\",\"hiveField\":\"_lineacce\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._rota\",\"hiveField\":\"_rota\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._gps\",\"hiveField\":\"_gps\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.sort\",\"hiveField\":\"sort\",\"hiveType\":\"int\"},{\"jsonPath\":\"properties.usertype\",\"hiveField\":\"usertype\",\"hiveType\":\"int\"},{\"jsonPath\":\"properties.coin\",\"hiveField\":\"coin\",\"hiveType\":\"double\"},{\"jsonPath\":\"properties.rate\",\"hiveField\":\"rate\",\"hiveType\":\"double\"},{\"jsonPath\":\"properties.rmb\",\"hiveField\":\"rmb\",\"hiveType\":\"double\"},{\"jsonPath\":\"properties._sdk\",\"hiveField\":\"_sdk\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._sdkver\",\"hiveField\":\"_sdkver\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.gps_province\",\"hiveField\":\"gps_province\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.gps_area\",\"hiveField\":\"gps_area\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.order_id\",\"hiveField\":\"order_id\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.browser\",\"hiveField\":\"browser\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.terminal\",\"hiveField\":\"terminal\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.ad\",\"hiveField\":\"ad\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.gameid\",\"hiveField\":\"gameid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.adid\",\"hiveField\":\"adid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.eventid\",\"hiveField\":\"eventid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.device_id\",\"hiveField\":\"device_id\",\"hiveType\":\"string\"},{\"jsonPath\":\"kafka_unique_id\",\"hiveField\":\"kafkauniqueid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.templateid\",\"hiveField\":\"templateid\",\"hiveType\":\"string\"}],\"partition\":[{\"name\":\"eventtype\",\"expression\":\"\'high\' as eventtype\"},{\"name\":\"ds\",\"expression\":\"from_unixtime(unix_timestamp(),\'yyyy-MM-dd\') as ds\"},{\"name\":\"hour\",\"expression\":\"from_unixtime(unix_timestamp(),\'yyyyMMddHH\') as hour\"}],\"where\":\"\"}\r\n');
INSERT INTO `t_kafka2hive_relation_config` VALUES (6, 'wutiao_medium', 'wutiao.odl_event_qkl_testnew', '{\"relation\":[{\"jsonPath\":\"ouid\",\"hiveField\":\"ouid\",\"hiveType\":\"string\"},{\"jsonPath\":\"timestamp\",\"hiveField\":\"timestamp\",\"hiveType\":\"long\"},{\"jsonPath\":\"properties._sst\",\"hiveField\":\"_sst\",\"hiveType\":\"long\"},{\"jsonPath\":\"did\",\"hiveField\":\"did\",\"hiveType\":\"string\"},{\"jsonPath\":\"event\",\"hiveField\":\"event\",\"hiveType\":\"string\"},{\"jsonPath\":\"project\",\"hiveField\":\"project\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._ip\",\"hiveField\":\"_ip\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._appver\",\"hiveField\":\"_appver\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._os\",\"hiveField\":\"_os\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._osver\",\"hiveField\":\"_osver\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._model\",\"hiveField\":\"_model\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._mfr\",\"hiveField\":\"_mfr\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._res\",\"hiveField\":\"_res\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._nettype\",\"hiveField\":\"_nettype\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._carrier\",\"hiveField\":\"_carrier\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._channel\",\"hiveField\":\"_channel\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.isvisitor\",\"hiveField\":\"isvisitor\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.step\",\"hiveField\":\"step\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.phone\",\"hiveField\":\"phone\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.reftype\",\"hiveField\":\"reftype\",\"hiveType\":\"long\"},{\"jsonPath\":\"properties.msg\",\"hiveField\":\"msg\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.type\",\"hiveField\":\"type\",\"hiveType\":\"long\"},{\"jsonPath\":\"properties.trace\",\"hiveField\":\"trace\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.staytime\",\"hiveField\":\"staytime\",\"hiveType\":\"long\"},{\"jsonPath\":\"properties.commentid\",\"hiveField\":\"commentid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.targetouid\",\"hiveField\":\"targetouid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.mediaid\",\"hiveField\":\"mediaid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.itemid\",\"hiveField\":\"itemid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.itemtype\",\"hiveField\":\"itemtype\",\"hiveType\":\"long\"},{\"jsonPath\":\"properties.itemlist\",\"hiveField\":\"itemlist\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.sortid\",\"hiveField\":\"sortid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.status\",\"hiveField\":\"status\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.shareplat\",\"hiveField\":\"shareplat\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.replyid\",\"hiveField\":\"replyid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.target\",\"hiveField\":\"target\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.timemachid\",\"hiveField\":\"timemachid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.isvalid\",\"hiveField\":\"isvalid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.subjectid\",\"hiveField\":\"subjectid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.keyword\",\"hiveField\":\"keyword\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.pos\",\"hiveField\":\"pos\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.userid\",\"hiveField\":\"userid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.inviterid\",\"hiveField\":\"inviterid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.familyid\",\"hiveField\":\"familyid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.contributionpoint\",\"hiveField\":\"contributionpoint\",\"hiveType\":\"double\"},{\"jsonPath\":\"properties.verifypoint\",\"hiveField\":\"verifypoint\",\"hiveType\":\"double\"},{\"jsonPath\":\"properties.incref\",\"hiveField\":\"incref\",\"hiveType\":\"long\"},{\"jsonPath\":\"properties._accemeter\",\"hiveField\":\"_accemeter\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._magfield\",\"hiveField\":\"_magfield\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._orient\",\"hiveField\":\"_orient\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._gyros\",\"hiveField\":\"_gyros\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._light\",\"hiveField\":\"_light\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._press\",\"hiveField\":\"_press\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._tempera\",\"hiveField\":\"_tempera\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._prox\",\"hiveField\":\"_prox\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._grav\",\"hiveField\":\"_grav\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._lineacce\",\"hiveField\":\"_lineacce\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._rota\",\"hiveField\":\"_rota\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._gps\",\"hiveField\":\"_gps\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.sort\",\"hiveField\":\"sort\",\"hiveType\":\"int\"},{\"jsonPath\":\"properties.usertype\",\"hiveField\":\"usertype\",\"hiveType\":\"int\"},{\"jsonPath\":\"properties.coin\",\"hiveField\":\"coin\",\"hiveType\":\"double\"},{\"jsonPath\":\"properties.rate\",\"hiveField\":\"rate\",\"hiveType\":\"double\"},{\"jsonPath\":\"properties.rmb\",\"hiveField\":\"rmb\",\"hiveType\":\"double\"},{\"jsonPath\":\"properties._sdk\",\"hiveField\":\"_sdk\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._sdkver\",\"hiveField\":\"_sdkver\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.gps_province\",\"hiveField\":\"gps_province\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.gps_area\",\"hiveField\":\"gps_area\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.order_id\",\"hiveField\":\"order_id\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.browser\",\"hiveField\":\"browser\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.terminal\",\"hiveField\":\"terminal\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.ad\",\"hiveField\":\"ad\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.gameid\",\"hiveField\":\"gameid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.adid\",\"hiveField\":\"adid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.eventid\",\"hiveField\":\"eventid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.device_id\",\"hiveField\":\"device_id\",\"hiveType\":\"string\"},{\"jsonPath\":\"kafka_unique_id\",\"hiveField\":\"kafkauniqueid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.templateid\",\"hiveField\":\"templateid\",\"hiveType\":\"string\"}],\"partition\":[{\"name\":\"eventtype\",\"expression\":\"\'medium\' as eventtype\"},{\"name\":\"ds\",\"expression\":\"from_unixtime(unix_timestamp(),\'yyyy-MM-dd\') as ds\"},{\"name\":\"hour\",\"expression\":\"from_unixtime(unix_timestamp(),\'yyyyMMddHH\') as hour\"}],\"where\":\"\"}\r\n');
INSERT INTO `t_kafka2hive_relation_config` VALUES (7, 'wutiao_low', 'wutiao.odl_event_qkl_testnew', '{\"relation\":[{\"jsonPath\":\"ouid\",\"hiveField\":\"ouid\",\"hiveType\":\"string\"},{\"jsonPath\":\"timestamp\",\"hiveField\":\"timestamp\",\"hiveType\":\"long\"},{\"jsonPath\":\"properties._sst\",\"hiveField\":\"_sst\",\"hiveType\":\"long\"},{\"jsonPath\":\"did\",\"hiveField\":\"did\",\"hiveType\":\"string\"},{\"jsonPath\":\"event\",\"hiveField\":\"event\",\"hiveType\":\"string\"},{\"jsonPath\":\"project\",\"hiveField\":\"project\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._ip\",\"hiveField\":\"_ip\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._appver\",\"hiveField\":\"_appver\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._os\",\"hiveField\":\"_os\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._osver\",\"hiveField\":\"_osver\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._model\",\"hiveField\":\"_model\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._mfr\",\"hiveField\":\"_mfr\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._res\",\"hiveField\":\"_res\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._nettype\",\"hiveField\":\"_nettype\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._carrier\",\"hiveField\":\"_carrier\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._channel\",\"hiveField\":\"_channel\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.isvisitor\",\"hiveField\":\"isvisitor\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.step\",\"hiveField\":\"step\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.phone\",\"hiveField\":\"phone\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.reftype\",\"hiveField\":\"reftype\",\"hiveType\":\"long\"},{\"jsonPath\":\"properties.msg\",\"hiveField\":\"msg\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.type\",\"hiveField\":\"type\",\"hiveType\":\"long\"},{\"jsonPath\":\"properties.trace\",\"hiveField\":\"trace\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.staytime\",\"hiveField\":\"staytime\",\"hiveType\":\"long\"},{\"jsonPath\":\"properties.commentid\",\"hiveField\":\"commentid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.targetouid\",\"hiveField\":\"targetouid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.mediaid\",\"hiveField\":\"mediaid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.itemid\",\"hiveField\":\"itemid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.itemtype\",\"hiveField\":\"itemtype\",\"hiveType\":\"long\"},{\"jsonPath\":\"properties.itemlist\",\"hiveField\":\"itemlist\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.sortid\",\"hiveField\":\"sortid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.status\",\"hiveField\":\"status\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.shareplat\",\"hiveField\":\"shareplat\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.replyid\",\"hiveField\":\"replyid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.target\",\"hiveField\":\"target\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.timemachid\",\"hiveField\":\"timemachid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.isvalid\",\"hiveField\":\"isvalid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.subjectid\",\"hiveField\":\"subjectid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.keyword\",\"hiveField\":\"keyword\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.pos\",\"hiveField\":\"pos\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.userid\",\"hiveField\":\"userid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.inviterid\",\"hiveField\":\"inviterid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.familyid\",\"hiveField\":\"familyid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.contributionpoint\",\"hiveField\":\"contributionpoint\",\"hiveType\":\"double\"},{\"jsonPath\":\"properties.verifypoint\",\"hiveField\":\"verifypoint\",\"hiveType\":\"double\"},{\"jsonPath\":\"properties.incref\",\"hiveField\":\"incref\",\"hiveType\":\"long\"},{\"jsonPath\":\"properties._accemeter\",\"hiveField\":\"_accemeter\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._magfield\",\"hiveField\":\"_magfield\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._orient\",\"hiveField\":\"_orient\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._gyros\",\"hiveField\":\"_gyros\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._light\",\"hiveField\":\"_light\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._press\",\"hiveField\":\"_press\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._tempera\",\"hiveField\":\"_tempera\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._prox\",\"hiveField\":\"_prox\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._grav\",\"hiveField\":\"_grav\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._lineacce\",\"hiveField\":\"_lineacce\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._rota\",\"hiveField\":\"_rota\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._gps\",\"hiveField\":\"_gps\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.sort\",\"hiveField\":\"sort\",\"hiveType\":\"int\"},{\"jsonPath\":\"properties.usertype\",\"hiveField\":\"usertype\",\"hiveType\":\"int\"},{\"jsonPath\":\"properties.coin\",\"hiveField\":\"coin\",\"hiveType\":\"double\"},{\"jsonPath\":\"properties.rate\",\"hiveField\":\"rate\",\"hiveType\":\"double\"},{\"jsonPath\":\"properties.rmb\",\"hiveField\":\"rmb\",\"hiveType\":\"double\"},{\"jsonPath\":\"properties._sdk\",\"hiveField\":\"_sdk\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties._sdkver\",\"hiveField\":\"_sdkver\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.gps_province\",\"hiveField\":\"gps_province\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.gps_area\",\"hiveField\":\"gps_area\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.order_id\",\"hiveField\":\"order_id\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.browser\",\"hiveField\":\"browser\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.terminal\",\"hiveField\":\"terminal\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.ad\",\"hiveField\":\"ad\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.gameid\",\"hiveField\":\"gameid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.adid\",\"hiveField\":\"adid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.eventid\",\"hiveField\":\"eventid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.device_id\",\"hiveField\":\"device_id\",\"hiveType\":\"string\"},{\"jsonPath\":\"kafka_unique_id\",\"hiveField\":\"kafkauniqueid\",\"hiveType\":\"string\"},{\"jsonPath\":\"properties.templateid\",\"hiveField\":\"templateid\",\"hiveType\":\"string\"}],\"partition\":[{\"name\":\"eventtype\",\"expression\":\"\'low\' as eventtype\"},{\"name\":\"ds\",\"expression\":\"from_unixtime(unix_timestamp(),\'yyyy-MM-dd\') as ds\"},{\"name\":\"hour\",\"expression\":\"from_unixtime(unix_timestamp(),\'yyyyMMddHH\') as hour\"}],\"where\":\"\"}\r\n');

SET FOREIGN_KEY_CHECKS = 1;