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

package eu.cdevreeze.xbrl4j.common.xpointer;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import eu.cdevreeze.yaidom4j.queryapi.ElementApi;

import java.util.Optional;

import static eu.cdevreeze.xbrl4j.model.Names.ID_QNAME;

/**
 * Utility class for XPointer processing.
 *
 * @author Chris de Vreeze
 */
public class XPointers {

    private XPointers() {
    }

    public static ImmutableList<XPointer> parseXPointers(String s) {
        if (s.startsWith("element(")) {
            return parseElementSchemePointers(s);
        } else {
            return ImmutableList.of(parseXPointer(s));
        }
    }

    public static XPointer parseXPointer(String s) {
        Preconditions.checkArgument(
                !s.startsWith("xpointer("),
                "In XBRL, only element scheme pointers and shorthand pointers are allowed"
        );

        if (s.startsWith("element(")) {
            return parseElementSchemePointer(s);
        } else {
            return new ShorthandPointer(s);
        }
    }

    public static ImmutableList<XPointer> parseElementSchemePointers(String s) {
        Preconditions.checkArgument(s.startsWith("element("));

        int indexOfNextPointer = s.indexOf("element(", 7);

        if (indexOfNextPointer < 0) {
            return ImmutableList.of(parseElementSchemePointer(s));
        } else {
            String s1 = s.substring(0, indexOfNextPointer);
            String s2 = s.substring(indexOfNextPointer);

            ImmutableList.Builder<XPointer> builder = ImmutableList.builder();
            builder.add(parseXPointer(s1));
            // Recursion
            builder.addAll(parseElementSchemePointers(s2));
            return builder.build();
        }
    }

    public static XPointer parseElementSchemePointer(String s) {
        Preconditions.checkArgument(s.startsWith("element("));
        Preconditions.checkArgument(s.endsWith(")"));
        String remainder = s.substring("element(".length(), s.length() - 1);

        if (remainder.startsWith("/")) {
            return new ChildSequencePointer(parseChildSequence(remainder));
        } else {
            if (remainder.contains("/")) {
                String id = remainder.substring(0, remainder.indexOf("/"));
                Preconditions.checkArgument(!id.isEmpty());
                ImmutableList<Integer> childSequence = parseChildSequence(remainder.substring(id.length()));
                return new IdChildSequencePointer(id, childSequence);
            } else {
                return new IdPointer(remainder);
            }
        }
    }

    private static ImmutableList<Integer> parseChildSequence(String path) {
        Preconditions.checkArgument(path.startsWith("/"));

        if (path.substring(1).contains("/")) {
            int indexOfNextSlash = path.indexOf("/", 1);
            ImmutableList.Builder<Integer> builder = ImmutableList.builder();
            builder.add(Integer.valueOf(path.substring(1, indexOfNextSlash)));
            // Recursion
            builder.addAll(parseChildSequence(path.substring(indexOfNextSlash)));
            return builder.build();
        } else {
            return ImmutableList.of(Integer.valueOf(path.substring(1)));
        }
    }

    public static <E extends ElementApi<E>> Optional<E> findElement(E rootElement, ImmutableList<XPointer> xpointers) {
        return xpointers.stream().flatMap(xp -> findElement(rootElement, xp).stream()).findFirst();
    }

    public static <E extends ElementApi<E>> Optional<E> findElement(E rootElement, XPointer xpointer) {
        if (xpointer instanceof ShorthandPointer p) {
            return findElement(rootElement, p);
        } else if (xpointer instanceof IdPointer p) {
            return findElement(rootElement, p);
        } else if (xpointer instanceof ChildSequencePointer p) {
            return findElement(rootElement, p);
        } else if (xpointer instanceof IdChildSequencePointer p) {
            return findElement(rootElement, p);
        } else {
            throw new IllegalStateException("Missing an XPointer subtype? That would be a programmer error");
        }
    }

    public static <E extends ElementApi<E>> Optional<E> findElement(E rootElement, ShorthandPointer xpointer) {
        return rootElement
                .elementStream(e -> e.attributeOption(ID_QNAME).stream().anyMatch(id -> id.equals(xpointer.id())))
                .findFirst();
    }

    public static <E extends ElementApi<E>> Optional<E> findElement(E rootElement, IdPointer xpointer) {
        return findElement(rootElement, new ShorthandPointer(xpointer.id()));
    }

    public static <E extends ElementApi<E>> Optional<E> findElement(E rootElement, ChildSequencePointer xpointer) {
        Preconditions.checkArgument(xpointer.childElementSequence().get(0).equals(1));

        Optional<E> result = Optional.of(rootElement);

        for (int pe : xpointer.childElementSequenceZeroBased().subList(1, xpointer.childElementSequence().size())) {
            if (result.isPresent()) {
                E currentElement = result.get();
                ImmutableList<E> childElements = currentElement.childElementStream().collect(ImmutableList.toImmutableList());

                if (pe >= childElements.size()) {
                    return Optional.empty();
                } else {
                    result = result.flatMap(che -> Optional.ofNullable(che.childElementStream().toList().get(pe)));
                }
            }
        }

        return result;
    }

    public static <E extends ElementApi<E>> Optional<E> findElement(E rootElement, IdChildSequencePointer xpointer) {
        Optional<E> result = findElement(rootElement, new ShorthandPointer(xpointer.id()));

        for (int pe : xpointer.childElementSequenceZeroBased()) {
            if (result.isPresent()) {
                E currentElement = result.get();
                ImmutableList<E> childElements = currentElement.childElementStream().collect(ImmutableList.toImmutableList());

                if (pe >= childElements.size()) {
                    return Optional.empty();
                } else {
                    result = result.flatMap(che -> Optional.ofNullable(che.childElementStream().toList().get(pe)));
                }
            }
        }

        return result;
    }
}
