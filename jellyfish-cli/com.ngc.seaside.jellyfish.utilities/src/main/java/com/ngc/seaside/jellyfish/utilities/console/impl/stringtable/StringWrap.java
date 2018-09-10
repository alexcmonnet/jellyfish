/**
 * UNCLASSIFIED
 * Northrop Grumman Proprietary
 * ____________________________
 *
 * Copyright (C) 2018, Northrop Grumman Systems Corporation
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains the property of
 * Northrop Grumman Systems Corporation. The intellectual and technical concepts
 * contained herein are proprietary to Northrop Grumman Systems Corporation and
 * may be covered by U.S. and Foreign Patents or patents in process, and are
 * protected by trade secret or copyright law. Dissemination of this information
 * or reproduction of this material is strictly forbidden unless prior written
 * permission is obtained from Northrop Grumman.
 */
package com.ngc.seaside.jellyfish.utilities.console.impl.stringtable;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides a methods to wrap a String based on a given length.
 *
 * @author justan.provence@ngc.com
 */
public class StringWrap {

   /**
    * Wrap the string based on the length and return the results in a list.
    *
    * @param str           the value to wrap.
    * @param wrapLength    the number of characters in each line.
    * @param wrapLongWords if the word is longer than the wrapLength it will wrap the word.
    * @return The str value broken up in to a list of strings with each string in the list being at
    * most wrapLength.
    */
   public static List<String> wrap(String str, int wrapLength, boolean wrapLongWords) {
      List<String> lines = new ArrayList<>();
      if (str == null) {
         return null;
      }

      if (wrapLength < 1) {
         wrapLength = 1;
      }
      int inputLineLength = str.length();
      int offset = 0;

      while ((inputLineLength - offset) > wrapLength) {
         if (str.charAt(offset) == ' ') {
            offset++;
            continue;
         }
         int spaceToWrapAt = str.lastIndexOf(' ', wrapLength + offset);

         if (spaceToWrapAt >= offset) {
            // normal case
            lines.add(str.substring(offset, spaceToWrapAt));

            offset = spaceToWrapAt + 1;

         } else {
            // really long word or URL
            if (wrapLongWords) {
               // wrap really long word one line at a time
               lines.add(str.substring(offset, wrapLength + offset));

               offset += wrapLength;
            } else {
               // do not wrap really long word, just extend beyond limit
               spaceToWrapAt = str.indexOf(' ', wrapLength + offset);
               if (spaceToWrapAt >= 0) {
                  lines.add(str.substring(offset, spaceToWrapAt));

                  offset = spaceToWrapAt + 1;
               } else {
                  lines.add(str.substring(offset));
                  offset = inputLineLength;
               }
            }
         }
      }

      // Whatever is left in line is short enough to just pass through
      lines.add(str.substring(offset));

      return lines;
   }

}
