/**
 * Copyright (C) 2012-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package models;

public class FormObject {
    
    public String name;
    
    public int primInt;
    public long primLong;
    public float primFloat;
    public double primDouble;
    
    public Integer objInt;
    public Long objLong;
    public Float objFloat;
    public Double objDouble;
    
    
    private String email;

    private boolean primBoolean;
    private byte primByte;
    private short primShort;
    private char primChar;
    
    private Boolean objBoolean;
    private Byte objByte;
    private Short objShort;
    private Character objChar;
    
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public boolean isPrimBoolean() {
        return primBoolean;
    }
    
    public void setPrimBoolean(boolean primBoolean) {
        this.primBoolean = primBoolean;
    }
    
    public byte getPrimByte() {
        return primByte;
    }
    
    public void setPrimByte(byte primByte) {
        this.primByte = primByte;
    }
    
    public short getPrimShort() {
        return primShort;
    }
    
    public void setPrimShort(short primShort) {
        this.primShort = primShort;
    }
    
    public char getPrimChar() {
        return primChar;
    }
    
    public void setPrimChar(char primChar) {
        this.primChar = primChar;
    }
    
    public Boolean getObjBoolean() {
        return objBoolean;
    }
    
    public void setObjBoolean(Boolean objBoolean) {
        this.objBoolean = objBoolean;
    }
    
    public Byte getObjByte() {
        return objByte;
    }
    
    public void setObjByte(Byte objByte) {
        this.objByte = objByte;
    }
    
    public Short getObjShort() {
        return objShort;
    }
    
    public void setObjShort(Short objShort) {
        this.objShort = objShort;
    }
    
    public Character getObjChar() {
        return objChar;
    }
    
    public void setObjChar(Character objChar) {
        this.objChar = objChar;
    }

}
