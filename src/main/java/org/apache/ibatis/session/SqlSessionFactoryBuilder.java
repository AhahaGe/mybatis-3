/**
 *    Copyright 2009-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.session;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.exceptions.ExceptionFactory;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;

/**
 * 采用了Builder设计模式
 * Builds {@link SqlSession} instances.
 *
 * @author Clinton Begin
 */
public class SqlSessionFactoryBuilder {

  public SqlSessionFactory build(Reader reader) {
    return build(reader, null, null);
  }

  public SqlSessionFactory build(Reader reader, String environment) {
    return build(reader, environment, null);
  }

  public SqlSessionFactory build(Reader reader, Properties properties) {
    return build(reader, null, properties);
  }

  public SqlSessionFactory build(Reader reader, String environment, Properties properties) {
    try {
      XMLConfigBuilder parser = new XMLConfigBuilder(reader, environment, properties);
      return build(parser.parse());
    } catch (Exception e) {
      throw ExceptionFactory.wrapException("Error building SqlSession.", e);
    } finally {
      ErrorContext.instance().reset();
      try {
        reader.close();
      } catch (IOException e) {
        // Intentionally ignore. Prefer previous error.
      }
    }
  }

  /**
   *
   * 1. SqlSessionFactoryBuilder根据传入的数据流生成Configuration对象
   *    1.1 根据传入的inputStream构建XMLConfigBuilder对象
   *    1.2 调用XMLConfigBuilder对象的parse方法生成Configuration对象
   *       1.2.1 根据传入的inputStream构建XMLConfigBuilder对象
   *         1.2.1.1 构建XPathParser对象
   *           1.2.1.1.1 设置validation，entityResolver，variables属性
   *           1.2.1.1.2 调用XPathFactory.newInstance()生成XPath实例
   *           1.2.1.1.3 根据传入的inputStream，之前设置的entityResolver，validation属性，然后调用DocumentBuilder.parse(inputSource)创建Document对象
   *         1.2.1.2 构建XMLConfigBuilder对象
   *           1.2.1.2.1 创建Configuration对象，设置typeAliasRegistry|typeHandlerRegistry|variables
   *           1.2.1.2.2 设置parsed为false，environment，parser为上一步创建的Parser
   *       1.2.2 调用XMLConfigBuilder对象的parse方法生成Configuration对象
   *         1.2.2.1 解析节点properties|settings|typeAliases|plugins|objectFactory|objectWrapperFactory|reflectorFactory|environments|databaseIdProvider|typeHandlers|mappers
   *    1.3 调用build方法传入Configuration对象创建DefaultSqlSessionFactory对象
   * 2. 然后根据Configuration对象创建默认的SqlSessionFactory
   *
   * @param inputStream 传入的Config.xml文件流
   * @return SqlSessionFactory对象
   */
  public SqlSessionFactory build(InputStream inputStream) {
    return build(inputStream, null, null);
  }

  public SqlSessionFactory build(InputStream inputStream, String environment) {
    return build(inputStream, environment, null);
  }

  public SqlSessionFactory build(InputStream inputStream, Properties properties) {
    return build(inputStream, null, properties);
  }

  public SqlSessionFactory build(InputStream inputStream, String environment, Properties properties) {
    try {
      //根据传入的inputStream构建XMLConfigBuilder对象
      //  构建XPathParser对象
      //    设置validation，entityResolver，variables属性
      //    调用XPathFactory.newInstance()生成XPath实例
      //    根据传入的inputStream，之前设置的entityResolver，validation属性，然后调用DocumentBuilder.parse(inputSource)创建Document对象
      //  构建XMLConfigBuilder对象
      //    创建Configuration对象，设置typeAliasRegistry|typeHandlerRegistry|variables
      //    设置parsed为false，environment，parser为上一步创建的Parser
      XMLConfigBuilder parser = new XMLConfigBuilder(inputStream, environment, properties);
      //调用XMLConfigBuilder对象的parse方法生成Configuration对象
        //解析节点properties|settings|typeAliases|plugins|objectFactory|objectWrapperFactory|reflectorFactory|environments|databaseIdProvider|typeHandlers|mappers
      //调用build方法传入Configuration对象创建DefaultSqlSessionFactory对象
      return build(parser.parse());
    } catch (Exception e) {
      throw ExceptionFactory.wrapException("Error building SqlSession.", e);
    } finally {
      ErrorContext.instance().reset();
      try {
        inputStream.close();
      } catch (IOException e) {
        // Intentionally ignore. Prefer previous error.
      }
    }
  }

  // 用户也可以传入Configuration对象来创建DefaultSqlSessionFactory对象
  public SqlSessionFactory build(Configuration config) {
    return new DefaultSqlSessionFactory(config);
  }

}
