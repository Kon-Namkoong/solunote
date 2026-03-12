/**
 * Copyright 2024 Solugate

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
/**
 * added by Solugate
 * 
 * Convert a diff array into a pretty HTML string.
 * @param {!Array.<!diff_match_patch.Diff>} diffs Array of diff tuples.
 * @return {Array.<string>} Two element Array, containing pretty printed
 *         html string of left and right string.
 */
diff_match_patch.prototype.diff_changed = function(diffs) {
  var left = [];
  var right = [];
  var pattern_amp = /&/g;
  var pattern_lt = /</g;
  var pattern_gt = />/g;
  var pattern_para = /\n/g;
  for (var x = 0; x < diffs.length; x++) {
    var op = diffs[x][0];    // Operation (insert, delete, equal)
    var data = diffs[x][1];  // Text of change.
    var text = data.replace(pattern_amp, '&amp;').replace(pattern_lt, '&lt;')
        .replace(pattern_gt, '&gt;').replace(pattern_para, '&para;<br>');
    switch (op) {
      case DIFF_INSERT:
        right[x] = '<em>' + text + '</em>';
        break;
      case DIFF_DELETE:
         left[x] = '<em>' + text + '</em>';
        break;
      case DIFF_EQUAL:
        left[x] = text;
		right[x] = text;
        break;
    }
  }
  return [left.join(''), right.join('')];
};