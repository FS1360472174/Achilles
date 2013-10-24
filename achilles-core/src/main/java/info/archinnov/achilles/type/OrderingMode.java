/**
 *
 * Copyright (C) 2012-2013 DuyHai DOAN
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
package info.archinnov.achilles.type;

public enum OrderingMode {
	DESCENDING(true, "DESC"), ASCENDING(false, "ASC");

	private boolean reverse;
	private String string;

	private OrderingMode(boolean reverse, String string) {
		this.reverse = reverse;
		this.string = string;
	}

    public OrderingMode reverse() {
        if(this == ASCENDING)
            return DESCENDING;
        else
            return ASCENDING;
    }

	public boolean isReverse() {
		return reverse;
	}

	public String asString() {
		return string;
	}
}
