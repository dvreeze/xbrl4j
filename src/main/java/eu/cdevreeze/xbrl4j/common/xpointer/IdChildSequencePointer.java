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

/**
 * Element scheme pointer starting with an ID, followed by a child sequence.
 * The child element sequence is 1-based, not 0-based.
 *
 * @author Chris de Vreeze
 */
public record IdChildSequencePointer(String id,
                                     ImmutableList<Integer> childElementSequence) implements ElementSchemePointer {

    public IdChildSequencePointer {
        Preconditions.checkArgument(!childElementSequence.isEmpty());
        Preconditions.checkArgument(childElementSequence.stream().allMatch(pe -> pe >= 1));
    }

    public ImmutableList<Integer> childElementSequenceZeroBased() {
        return childElementSequence().stream().map(i -> i - 1).collect(ImmutableList.toImmutableList());
    }
}
