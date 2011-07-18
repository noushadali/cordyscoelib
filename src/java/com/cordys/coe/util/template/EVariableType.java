package com.cordys.coe.util.template;

/**
 * Defines valid variable types.
 *
 * @author  mpoyhone
 */
public enum EVariableType
{
    /**
     * NOM XML.
     */
    XML,
    /**
     * A string. Byte variables are base64 encoded.
     */
    STRING,
    /**
     * A string containing bytes as hexadecimal characters.
     */
    HEXSTRING,
    /**
     * Binary data as a base64 encoded string.
     */
    BASE64;

    /**
     * Returns the default type used when a type is not explicitly specified.
     *
     * @return  The default type.
     */
    public static EVariableType getDefault()
    {
        return STRING;
    }
}
