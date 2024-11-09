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

package eu.cdevreeze.xbrl4j.model.ref;

import eu.cdevreeze.xbrl4j.model.link.Part;

/**
 * A "ref:Sentence" element. See schema http://www.xbrl.org/2006/ref-2006-02-27.xsd.
 *
 * @author Chris de Vreeze
 */
public interface RefSentence extends RefElement, Part {
}
