package com.example.common;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

/**
 * 添加全局的json反序列化设置
 * 这个JsonComponent类定义了一个自定义的Json反序列化器StringDeserializer,用来防止XSS攻击。
 * 通过@JsonComponent注解,这个反序列化器会自动被Spring的Json序列化/反序列化模块扫描到。
 * 然后在对String类型进行Json反序列化时,就会使用这个自定义的反序列化器。
 */
@JsonComponent
public class GlobalJsonDeserializer {

    /**
     * 字符串反序列化器
     * 过滤特殊字符，解决 XSS 攻击
     * 主要逻辑:
     *  *
     *  * StringDeserializer继承自JsonDeserializer,用于反序列化String类型。
     *  * 在deserialize方法中,会先调用jsonParser获取原始的字符串值。
     *  * 然后使用StringEscapeUtils.escapeHtml4方法对字符串进行转义,这可以消除可能的XSS攻击代码。
     *  * 最终返回转义后的安全字符串。
     */
    public static class StringDeserializer extends JsonDeserializer<String> {

        @Override
        public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            // 防止xss攻击
            return StringEscapeUtils.escapeHtml4(jsonParser.getValueAsString());
        }
    }
}
