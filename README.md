
##Mac安装kafka 并生产消息和消费消息
- 1. 安装kafka

  ```
  $   brew install kafka
  ```
  - 1、 安装过程将依赖安装 zookeeper
  - 2、 软件位置
  
      ```
    /usr/local/Cellar/zookeeper
    /usr/local/Cellar/kafka
      ```
  - 3、 配置文件位置

        ```
     /usr/local/etc/kafka/zookeeper.properties
     /usr/local/etc/kafka/server.properties
        ```
备注：后续操作均需进入 /usr/local/Cellar/kafka/1.0.0/bin 目录下。

- 2、启动zookeeper

      ```
      zookeeper-server-start /usr/local/etc/kafka/zookeeper.properties &
      ```
- 3、启动kafka服务

    ```
    kafka-server-start /usr/local/etc/kafka/server.properties &
    ```
- 4、 创建topic

     ```
     kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test1
     ```
- 5、查看创建的topic

  ```
  kafka-topics --list --zookeeper localhost:2181  
  ```
- 6、生产数据

  ```
  kafka-console-producer --broker-list localhost:9092 --topic test1
  ```
- 7、消费数据

  ```
  kafka-console-consumer --bootstrap-server 127.0.0.1:9092 --topic test1 --from-beginning
  ```
备注：--from-beginning  将从第一个消息还是接收

配置文件加载
![](https://upload-images.jianshu.io/upload_images/325120-56683d2898c9fa0f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/800)


 ![](https://upload-images.jianshu.io/upload_images/325120-73973d84150ed0a4.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/800)
 
 ![](https://upload-images.jianshu.io/upload_images/325120-235b2b7aaf86e2ab.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/800)
 
 ![](https://upload-images.jianshu.io/upload_images/325120-fe415babc1c55497.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/800)

 ![](https://upload-images.jianshu.io/upload_images/325120-8dd96b92b3c5d5c8.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/800)
