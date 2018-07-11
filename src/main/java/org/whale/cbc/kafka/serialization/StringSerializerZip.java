package org.whale.cbc.kafka.serialization;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.StringSerializer;
import org.whale.cbc.kafka.util.ZipStrUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @Author thuglife
 * @DATE 2018/3/14
 * @DESCRIPTION :
 */
public class StringSerializerZip extends StringSerializer {
    private String encoding = "UTF8";
    @Override
    public byte[] serialize(String topic, String data) {
        try {
            data = ZipStrUtil.compress(data);
            return data == null?null:data.getBytes(this.encoding);
        } catch (UnsupportedEncodingException var4) {
            throw new SerializationException("Error when serializing string to byte[] due to unsupported encoding " + this.encoding);
        }catch (IOException var5){
            throw new SerializationException("Error when deserializing :"+var5.getMessage());
        }
    }
}
