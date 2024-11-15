/*
 * Copyright 2024-2024 Chris de Vreeze
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cdevreeze.xbrl4j.common.dom.saxon;

import eu.cdevreeze.yaidom4j.queryapi.ElementPredicateFactoryApi;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import java.util.function.Predicate;

/**
 * Several element predicates.
 *
 * @author Chris de Vreeze
 */
public class SaxonElementPredicates {

    private SaxonElementPredicates() {
    }

    private static final Factory factory = new Factory();

    public static Predicate<SaxonNodes.Element> hasName(QName name) {
        return factory.hasName(name);
    }

    public static Predicate<SaxonNodes.Element> hasName(String namespace, String localName) {
        return factory.hasName(namespace, localName);
    }

    public static Predicate<SaxonNodes.Element> hasName(String noNamespaceName) {
        return factory.hasName(noNamespaceName);
    }

    public static Predicate<SaxonNodes.Element> hasAttributeWithName(QName attrName) {
        return factory.hasAttributeWithName(attrName);
    }

    public static Predicate<SaxonNodes.Element> hasAttributeWithName(String attrNamespace, String attrLocalName) {
        return factory.hasAttributeWithName(attrNamespace, attrLocalName);
    }

    public static Predicate<SaxonNodes.Element> hasAttributeWithName(String attrNoNamespaceName) {
        return factory.hasAttributeWithName(attrNoNamespaceName);
    }

    public static Predicate<SaxonNodes.Element> hasAttributeValue(QName attrName, Predicate<String> attrValuePredicate) {
        return factory.hasAttributeValue(attrName, attrValuePredicate);
    }

    public static Predicate<SaxonNodes.Element> hasAttributeValue(String attrNamespace, String attrLocalName, Predicate<String> attrValuePredicate) {
        return factory.hasAttributeValue(attrNamespace, attrLocalName, attrValuePredicate);
    }

    public static Predicate<SaxonNodes.Element> hasAttributeValue(String attrNoNamespaceName, Predicate<String> attrValuePredicate) {
        return factory.hasAttributeValue(attrNoNamespaceName, attrValuePredicate);
    }

    public static Predicate<SaxonNodes.Element> hasAttributeValue(QName attrName, String attrValue) {
        return factory.hasAttributeValue(attrName, attrValue);
    }

    public static Predicate<SaxonNodes.Element> hasAttributeValue(String attrNamespace, String attrLocalName, String attrValue) {
        return factory.hasAttributeValue(attrNamespace, attrLocalName, attrValue);
    }

    public static Predicate<SaxonNodes.Element> hasAttributeValue(String attrNoNamespaceName, String attrValue) {
        return factory.hasAttributeValue(attrNoNamespaceName, attrValue);
    }

    public static Predicate<SaxonNodes.Element> hasOnlyText(Predicate<String> textPredicate) {
        return factory.hasOnlyText(textPredicate);
    }

    public static Predicate<SaxonNodes.Element> hasOnlyText(String text) {
        return factory.hasOnlyText(text);
    }

    public static Predicate<SaxonNodes.Element> hasOnlyStrippedText(String text) {
        return factory.hasOnlyStrippedText(text);
    }

    /**
     * Element predicate factory, implementing a generic interface that is useful for yaidom4j querying code
     * that generalizes over element implementations.
     */
    public static final class Factory implements ElementPredicateFactoryApi<SaxonNodes.Element> {

        @Override
        public Predicate<SaxonNodes.Element> hasName(QName name) {
            return e -> e.elementName().equals(name);
        }

        @Override
        public Predicate<SaxonNodes.Element> hasName(String namespace, String localName) {
            return e -> e.elementName().getNamespaceURI().equals(namespace) && e.elementName().getLocalPart().equals(localName);
        }

        @Override
        public Predicate<SaxonNodes.Element> hasName(String noNamespaceName) {
            return hasName(XMLConstants.NULL_NS_URI, noNamespaceName);
        }

        @Override
        public Predicate<SaxonNodes.Element> hasAttributeWithName(QName attrName) {
            return e -> e.attributeOption(attrName).isPresent();
        }

        @Override
        public Predicate<SaxonNodes.Element> hasAttributeWithName(String attrNamespace, String attrLocalName) {
            return hasAttributeWithName(new QName(attrNamespace, attrLocalName));
        }

        @Override
        public Predicate<SaxonNodes.Element> hasAttributeWithName(String attrNoNamespaceName) {
            return hasAttributeWithName(new QName(attrNoNamespaceName));
        }

        @Override
        public Predicate<SaxonNodes.Element> hasAttributeValue(QName attrName, Predicate<String> attrValuePredicate) {
            return e -> e.attributes().entrySet().stream()
                    .anyMatch(kv -> kv.getKey().equals(attrName) && attrValuePredicate.test(kv.getValue()));
        }

        @Override
        public Predicate<SaxonNodes.Element> hasAttributeValue(String attrNamespace, String attrLocalName, Predicate<String> attrValuePredicate) {
            return hasAttributeValue(new QName(attrNamespace, attrLocalName), attrValuePredicate);
        }

        @Override
        public Predicate<SaxonNodes.Element> hasAttributeValue(String attrNoNamespaceName, Predicate<String> attrValuePredicate) {
            return hasAttributeValue(new QName(attrNoNamespaceName), attrValuePredicate);
        }

        @Override
        public Predicate<SaxonNodes.Element> hasAttributeValue(QName attrName, String attrValue) {
            return hasAttributeValue(attrName, s -> s.equals(attrValue));
        }

        @Override
        public Predicate<SaxonNodes.Element> hasAttributeValue(String attrNamespace, String attrLocalName, String attrValue) {
            return hasAttributeValue(new QName(attrNamespace, attrLocalName), attrValue);
        }

        @Override
        public Predicate<SaxonNodes.Element> hasAttributeValue(String attrNoNamespaceName, String attrValue) {
            return hasAttributeValue(new QName(attrNoNamespaceName), attrValue);
        }

        @Override
        public Predicate<SaxonNodes.Element> hasOnlyText(Predicate<String> textPredicate) {
            return e -> e.childNodeStream().allMatch(ch -> ch instanceof SaxonNodes.Text) &&
                    textPredicate.test(e.text());
        }

        @Override
        public Predicate<SaxonNodes.Element> hasOnlyText(String text) {
            return hasOnlyText(s -> s.equals(text));
        }

        @Override
        public Predicate<SaxonNodes.Element> hasOnlyStrippedText(String text) {
            return hasOnlyText(s -> s.strip().equals(text));
        }
    }
}
