CREATE TABLE `t_file` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `file_save_name` varchar(255) DEFAULT NULL,
  `file_name` varchar(255) DEFAULT NULL,
  `file_type` varchar(10) DEFAULT NULL,
  `file_size` varchar(100) DEFAULT NULL,
  `file_info` varchar(1000) DEFAULT NULL,
  `file_up_time` varchar(25) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;