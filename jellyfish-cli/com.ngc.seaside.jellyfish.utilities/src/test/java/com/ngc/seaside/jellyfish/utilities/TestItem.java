package com.ngc.seaside.jellyfish.utilities;

import java.util.Objects;

/**
 * @author justan.provence@ngc.com
 */
public class TestItem {
  private String param1;
  private String param2;
  private String param3;

  public TestItem(String param1, String param2, String param3) {
    this.param1 = param1;
    this.param2 = param2;
    this.param3 = param3;
  }

  public String getParam1() {
    return param1;
  }

  public String getParam2() {
    return param2;
  }

  public String getParam3() {
    return param3;
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        param1, param2, param3
    );
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == this) {
      return true;
    }

    if(!(obj instanceof TestItem)) {
      return false;
    }
    TestItem that = (TestItem)obj;
    return Objects.equals(param1, that.param1) &&
        Objects.equals(param2, that.param2) &&
        Objects.equals(param3, that.param3);
  }
}