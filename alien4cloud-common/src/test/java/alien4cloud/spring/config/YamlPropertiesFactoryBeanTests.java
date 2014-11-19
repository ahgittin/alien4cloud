/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package alien4cloud.spring.config;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.yaml.snakeyaml.Yaml;

import alien4cloud.spring.config.YamlProcessor.ResolutionMethod;

/**
 * @author Dave Syer
 * 
 */
public class YamlPropertiesFactoryBeanTests {

    @Test
    public void testLoadResource() throws Exception {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(new Resource[] { new ByteArrayResource("foo: bar\nspam:\n  foo: baz".getBytes()) });
        Properties properties = factory.getObject();
        assertEquals("bar", properties.get("foo"));
        assertEquals("baz", properties.get("spam.foo"));
    }

    @Test
    public void testLoadResourcesWithOverride() throws Exception {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(new Resource[] { new ByteArrayResource("foo: bar\nspam:\n  foo: baz".getBytes()),
                new ByteArrayResource("foo:\n  bar: spam".getBytes()) });
        Properties properties = factory.getObject();
        assertEquals("bar", properties.get("foo"));
        assertEquals("baz", properties.get("spam.foo"));
        assertEquals("spam", properties.get("foo.bar"));
    }

    @Test
    public void testLoadResourceWithMultipleDocuments() throws Exception {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(new Resource[] { new ByteArrayResource("foo: bar\nspam: baz\n---\nfoo: bag".getBytes()) });
        Properties properties = factory.getObject();
        assertEquals("bag", properties.get("foo"));
        assertEquals("baz", properties.get("spam"));
    }

    @Test
    public void testLoadResourceWithSelectedDocuments() throws Exception {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(new Resource[] { new ByteArrayResource("foo: bar\nspam: baz\n---\nfoo: bag\nspam: bad".getBytes()) });
        factory.setDocumentMatchers(Collections.singletonMap("foo", "bag"));
        Properties properties = factory.getObject();
        assertEquals("bag", properties.get("foo"));
        assertEquals("bad", properties.get("spam"));
    }

    @Test
    public void testLoadResourceWithDefaultMatch() throws Exception {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setMatchDefault(true);
        factory.setResources(new Resource[] { new ByteArrayResource("one: two\n---\nfoo: bar\nspam: baz\n---\nfoo: bag\nspam: bad".getBytes()) });
        factory.setDocumentMatchers(Collections.singletonMap("foo", "bag"));
        Properties properties = factory.getObject();
        assertEquals("bag", properties.get("foo"));
        assertEquals("bad", properties.get("spam"));
        assertEquals("two", properties.get("one"));
    }

    @Test
    public void testLoadResourceWithDefaultMatchSkippingMissedMatch() throws Exception {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setMatchDefault(true);
        factory.setResources(new Resource[] { new ByteArrayResource("one: two\n---\nfoo: bag\nspam: bad\n---\nfoo: bar\nspam: baz".getBytes()) });
        factory.setDocumentMatchers(Collections.singletonMap("foo", "bag"));
        Properties properties = factory.getObject();
        assertEquals("bag", properties.get("foo"));
        assertEquals("bad", properties.get("spam"));
        assertEquals("two", properties.get("one"));
    }

    @Test
    public void testLoadNonExistentResource() throws Exception {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResolutionMethod(ResolutionMethod.OVERRIDE_AND_IGNORE);
        factory.setResources(new Resource[] { new ClassPathResource("no-such-file.yml") });
        Properties properties = factory.getObject();
        assertEquals(0, properties.size());
    }

    @Test
    public void testLoadNull() throws Exception {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(new Resource[] { new ByteArrayResource("foo: bar\nspam:".getBytes()) });
        Properties properties = factory.getObject();
        assertEquals("bar", properties.get("foo"));
        assertEquals("", properties.get("spam"));
    }

    @Test
    public void testLoadArrayOfString() throws Exception {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(new Resource[] { new ByteArrayResource("foo:\n- bar\n- baz".getBytes()) });
        Properties properties = factory.getObject();
        assertEquals("bar", properties.get("foo[0]"));
        assertEquals("baz", properties.get("foo[1]"));
        assertEquals("bar,baz", properties.get("foo"));
    }

    @Test
    public void testLoadArrayOfObject() throws Exception {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(new Resource[] { new ByteArrayResource("foo:\n- bar:\n    spam: crap\n- baz\n- one: two\n  three: four".getBytes()) });
        Properties properties = factory.getObject();
        // System.err.println(properties);
        assertEquals("crap", properties.get("foo[0].bar.spam"));
        assertEquals("baz", properties.get("foo[1]"));
        assertEquals("two", properties.get("foo[2].one"));
        assertEquals("four", properties.get("foo[2].three"));
        assertEquals("{bar={spam=crap}},baz,{one=two, three=four}", properties.get("foo"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testYaml() {
        Yaml yaml = new Yaml();
        Map<String, ?> map = (Map<String, Object>) yaml.loadAs("foo: bar\nspam:\n  foo: baz", Map.class);
        assertEquals("bar", map.get("foo"));
        assertEquals("baz", ((Map<String, Object>) map.get("spam")).get("foo"));
    }

}