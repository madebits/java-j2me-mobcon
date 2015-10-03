package com.thoughtworks.qdox.model;

import java.io.Serializable;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.1 $
 */
public interface DocletTagFactory extends Serializable {
    DocletTag createDocletTag(String tag, String text);
}
