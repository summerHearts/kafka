package org.whale.cbc.kafka.serialization;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.whale.cbc.kafka.util.ZipStrUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @Author thuglife
 * @DATE 2018/3/14
 * @DESCRIPTION :
 */
public class StringDeserializerZip extends StringDeserializer {
    private String encoding = "UTF8";
    @Override
    public String deserialize(String topic, byte[] data) {
        try {
            return data == null?null: ZipStrUtil.unCompress(new String(data, this.encoding));
        } catch (UnsupportedEncodingException var4) {
            throw new SerializationException("Error when deserializing byte[] to string due to unsupported encoding " + this.encoding);
        } catch (IOException var5){
            throw new SerializationException("Error when deserializing :"+var5.getMessage());
        }
    }
}
