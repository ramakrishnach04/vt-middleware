/*
  $Id$

  Copyright (C) 2003-2012 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive.control;

import java.nio.ByteBuffer;
import org.ldaptive.LdapUtils;
import org.ldaptive.ResultCode;
import org.ldaptive.asn1.AbstractParseHandler;
import org.ldaptive.asn1.DERParser;
import org.ldaptive.asn1.DERPath;
import org.ldaptive.asn1.IntegerType;
import org.ldaptive.asn1.OctetStringType;

/**
 * Response control for server side sorting. See RFC 2891. Control is defined
 * as:
 *
 * <pre>
       SortResult ::= SEQUENCE {
          sortResult  ENUMERATED {
              success                   (0), -- results are sorted
              operationsError           (1), -- server internal failure
              timeLimitExceeded         (3), -- timelimit reached before
                                             -- sorting was completed
              strongAuthRequired        (8), -- refused to return sorted
                                             -- results via insecure
                                             -- protocol
              adminLimitExceeded       (11), -- too many matching entries
                                             -- for the server to sort
              noSuchAttribute          (16), -- unrecognized attribute
                                             -- type in sort key
              inappropriateMatching    (18), -- unrecognized or
                                             -- inappropriate matching
                                             -- rule in sort key
              insufficientAccessRights (50), -- refused to return sorted
                                             -- results to this client
              busy                     (51), -- too busy to process
              unwillingToPerform       (53), -- unable to sort
              other                    (80)
              },
        attributeType [0] AttributeDescription OPTIONAL }
 * </pre>
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SortResponseControl extends AbstractControl
  implements ResponseControl
{

  /** OID of this control. */
  public static final String OID = "1.2.840.113556.1.4.474";

  /** hash code seed. */
  private static final int HASH_CODE_SEED = 733;

  /** Result of the server side sorting. */
  private ResultCode sortResult;

  /** Failed attribute name. */
  private String attributeName;


  /** Default constructor. */
  public SortResponseControl()
  {
    super(OID);
  }


  /**
   * Creates a new sort response control.
   *
   * @param  critical  whether this control is critical
   */
  public SortResponseControl(final boolean critical)
  {
    super(OID, critical);
  }


  /**
   * Creates a new sort response control.
   *
   * @param  code  result of the sort
   * @param  critical  whether this control is critical
   */
  public SortResponseControl(final ResultCode code, final boolean critical)
  {
    super(OID, critical);
    setSortResult(code);
  }


  /**
   * Creates a new sort response control.
   *
   * @param  code  result of the sort
   * @param  attrName  name of the failed attribute
   * @param  critical  whether this control is critical
   */
  public SortResponseControl(
    final ResultCode code,
    final String attrName,
    final boolean critical)
  {
    super(OID, critical);
    setSortResult(code);
    setAttributeName(attrName);
  }


  /**
   * Returns the result code of the server side sort.
   *
   * @return  result code
   */
  public ResultCode getSortResult()
  {
    return sortResult;
  }


  /**
   * Sets the result code of the server side sort.
   *
   * @param  code  result code
   */
  public void setSortResult(final ResultCode code)
  {
    sortResult = code;
  }


  /**
   * Returns the attribute name that caused the sort to fail.
   *
   * @return  attribute name
   */
  public String getAttributeName()
  {
    return attributeName;
  }


  /**
   * Sets the attribute name that caused the sort to fail.
   *
   * @param  name  of an attribute
   */
  public void setAttributeName(final String name)
  {
    attributeName = name;
  }


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    return
      LdapUtils.computeHashCode(
        HASH_CODE_SEED,
        getOID(),
        getCriticality(),
        sortResult,
        attributeName);
  }


  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d::criticality=%s, sortResult=%s, attributeName=%s]",
        getClass().getName(),
        hashCode(),
        getCriticality(),
        sortResult,
        attributeName);
  }


  /** {@inheritDoc} */
  @Override
  public void decode(final byte[] berValue)
  {
    final DERParser parser = new DERParser();
    parser.registerHandler(SortResultHandler.PATH, new SortResultHandler(this));
    parser.registerHandler(
      AttributeTypeHandler.PATH, new AttributeTypeHandler(this));
    parser.parse(ByteBuffer.wrap(berValue));
  }


  /** Parse handler implementation for the sort result. */
  private static class SortResultHandler
    extends AbstractParseHandler<SortResponseControl>
  {

    /** DER path to result code. */
    public static final DERPath PATH = new DERPath("/SEQ/ENUM");


    /**
     * Creates a new sort result handler.
     *
     * @param  control  to configure
     */
    public SortResultHandler(final SortResponseControl control)
    {
      super(control);
    }


    /** {@inheritDoc} */
    @Override
    public void handle(final DERParser parser, final ByteBuffer encoded)
    {
      final int resultValue = IntegerType.decode(encoded).intValue();
      final ResultCode rc = ResultCode.valueOf(resultValue);
      if (rc == null) {
        throw new IllegalArgumentException(
          "Unknown result code " + resultValue);
      }
      getObject().setSortResult(rc);
    }
  }


  /** Parse handler implementation for the attribute type. */
  private static class AttributeTypeHandler
    extends AbstractParseHandler<SortResponseControl>
  {

    /** DER path to attr value. */
    public static final DERPath PATH = new DERPath("/SEQ/CTX(1)");


    /**
     * Creates a new attribute type handler.
     *
     * @param  control  to configure
     */
    public AttributeTypeHandler(final SortResponseControl control)
    {
      super(control);
    }


    /** {@inheritDoc} */
    @Override
    public void handle(final DERParser parser, final ByteBuffer encoded)
    {
      getObject().setAttributeName(OctetStringType.decode(encoded));
    }
  }
}
