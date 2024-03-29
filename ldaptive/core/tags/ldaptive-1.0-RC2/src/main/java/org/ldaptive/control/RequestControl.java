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

/**
 * Marker interface for ldap request controls.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface RequestControl extends Control
{


  /**
   * Provides the BER encoding of this control.
   *
   * @return  BER encoded request control
   */
  byte[] encode();
}
