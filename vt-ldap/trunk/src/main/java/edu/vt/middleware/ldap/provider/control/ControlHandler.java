/*
  $Id$

  Copyright (C) 2003-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.provider.control;

/**
 * Handles provider specific request and response controls.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface ControlHandler
{


  /**
   * Returns the OID of the control processed by this instance.
   *
   * @return  control oid
   */
  String getOID();
}
