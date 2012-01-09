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
package org.ldaptive.provider.apache;

import java.io.IOException;
import org.apache.directory.ldap.client.api.CramMd5Request;
import org.apache.directory.ldap.client.api.DigestMd5Request;
import org.apache.directory.ldap.client.api.GssApiRequest;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.apache.directory.shared.ldap.model.entry.Modification;
import org.apache.directory.shared.ldap.model.exception.LdapOperationException;
import org.apache.directory.shared.ldap.model.message.AddRequestImpl;
import org.apache.directory.shared.ldap.model.message.AddResponse;
import org.apache.directory.shared.ldap.model.message.BindRequestImpl;
import org.apache.directory.shared.ldap.model.message.BindResponse;
import org.apache.directory.shared.ldap.model.message.CompareRequestImpl;
import org.apache.directory.shared.ldap.model.message.CompareResponse;
import org.apache.directory.shared.ldap.model.message.Control;
import org.apache.directory.shared.ldap.model.message.DeleteRequestImpl;
import org.apache.directory.shared.ldap.model.message.DeleteResponse;
import org.apache.directory.shared.ldap.model.message.ModifyDnRequestImpl;
import org.apache.directory.shared.ldap.model.message.ModifyDnResponse;
import org.apache.directory.shared.ldap.model.message.ModifyRequestImpl;
import org.apache.directory.shared.ldap.model.message.ModifyResponse;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.ldaptive.AddRequest;
import org.ldaptive.BindRequest;
import org.ldaptive.CompareRequest;
import org.ldaptive.DeleteRequest;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapException;
import org.ldaptive.ModifyRequest;
import org.ldaptive.RenameRequest;
import org.ldaptive.Response;
import org.ldaptive.ResultCode;
import org.ldaptive.provider.Connection;
import org.ldaptive.provider.ControlProcessor;
import org.ldaptive.provider.SearchIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Apache LDAP provider implementation of ldap operations.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class ApacheLdapConnection implements Connection
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Ldap connection. */
  private LdapNetworkConnection connection;

  /** Result codes to retry operations on. */
  private ResultCode[] operationRetryResultCodes;

  /** Control processor. */
  private ControlProcessor<Control> controlProcessor;


  /**
   * Creates a new apache ldap connection.
   *
   * @param  lc  ldap connection
   */
  public ApacheLdapConnection(final LdapNetworkConnection lc)
  {
    connection = lc;
  }


  /**
   * Returns the result codes to retry operations on.
   *
   * @return  result codes
   */
  public ResultCode[] getOperationRetryResultCodes()
  {
    return operationRetryResultCodes;
  }


  /**
   * Sets the result codes to retry operations on.
   *
   * @param  codes  result codes
   */
  public void setOperationRetryResultCodes(final ResultCode[] codes)
  {
    operationRetryResultCodes = codes;
  }


  /**
   * Returns the control processor.
   *
   * @return  control processor
   */
  public ControlProcessor<Control> getControlProcessor()
  {
    return controlProcessor;
  }


  /**
   * Sets the control processor.
   *
   * @param  processor  control processor
   */
  public void setControlProcessor(final ControlProcessor<Control> processor)
  {
    controlProcessor = processor;
  }


  /**
   * Returns the underlying ldap connection.
   *
   * @return  ldap connection
   */
  public LdapNetworkConnection getLdapConnection()
  {
    return connection;
  }


  /** {@inheritDoc} */
  @Override
  public void close()
    throws LdapException
  {
    if (connection != null) {
      try {
        if (connection.isConnected()) {
          connection.unBind();
        }
      } catch (
        org.apache.directory.shared.ldap.model.exception.LdapException e) {
        logger.error("Error unbinding from LDAP", e);
      }
      try {
        connection.close();
      } catch (IOException e) {
        throw new LdapException(e);
      } finally {
        connection = null;
      }
    }
  }


  /** {@inheritDoc} */
  @Override
  public Response<Void> bind(final BindRequest request)
    throws LdapException
  {
    Response<Void> response = null;
    if (request.getSaslConfig() != null) {
      response = saslBind(request);
    } else if (request.getDn() == null && request.getCredential() == null) {
      response = anonymousBind(request);
    } else {
      response = simpleBind(request);
    }
    return response;
  }


  /**
   * Performs an anonymous bind.
   *
   * @param  request  to bind with
   *
   * @return  bind response
   *
   * @throws  LdapException  if an error occurs
   */
  protected Response<Void> anonymousBind(final BindRequest request)
    throws LdapException
  {
    Response<Void> response = null;
    try {
      final BindRequestImpl bri = new BindRequestImpl();
      if (request.getControls() != null) {
        bri.addAllControls(
          controlProcessor.processRequestControls(request.getControls()));
      }

      final BindResponse br = connection.bind(bri);
      ApacheLdapUtil.throwOperationException(
        operationRetryResultCodes,
        br.getLdapResult().getResultCode());
      response = new Response<Void>(
        null,
        ResultCode.valueOf(br.getLdapResult().getResultCode().getResultCode()),
        ApacheLdapUtil.processResponseControls(
          controlProcessor,
          request.getControls(),
          br));
    } catch (LdapOperationException e) {
      ApacheLdapUtil.throwOperationException(operationRetryResultCodes, e);
    } catch (org.apache.directory.shared.ldap.model.exception.LdapException e) {
      throw new LdapException(e);
    } catch (IOException e) {
      throw new LdapException(e);
    }
    return response;
  }


  /**
   * Performs a simple bind.
   *
   * @param  request  to bind with
   *
   * @return  bind response
   *
   * @throws  LdapException  if an error occurs
   */
  protected Response<Void> simpleBind(final BindRequest request)
    throws LdapException
  {
    Response<Void> response = null;
    try {
      final BindRequestImpl bri = new BindRequestImpl();
      if (request.getControls() != null) {
        bri.addAllControls(
          controlProcessor.processRequestControls(request.getControls()));
      }
      bri.setVersion3(true);
      bri.setSimple(true);
      bri.setName(new Dn(request.getDn()));
      bri.setCredentials(request.getCredential().getBytes());

      final BindResponse br = connection.bind(bri);
      ApacheLdapUtil.throwOperationException(
        operationRetryResultCodes,
        br.getLdapResult().getResultCode());
      response = new Response<Void>(
        null,
        ResultCode.valueOf(br.getLdapResult().getResultCode().getResultCode()),
        ApacheLdapUtil.processResponseControls(
          controlProcessor,
          request.getControls(),
          br));
    } catch (LdapOperationException e) {
      ApacheLdapUtil.throwOperationException(operationRetryResultCodes, e);
    } catch (org.apache.directory.shared.ldap.model.exception.LdapException e) {
      throw new LdapException(e);
    } catch (IOException e) {
      throw new LdapException(e);
    }
    return response;
  }


  /**
   * Performs a sasl bind.
   *
   * @param  request  to bind with
   *
   * @return  bind response
   *
   * @throws  LdapException  if an error occurs
   */
  protected Response<Void> saslBind(final BindRequest request)
    throws LdapException
  {
    Response<Void> response = null;
    try {
      BindResponse br = null;
      switch (request.getSaslConfig().getMechanism()) {

      case EXTERNAL:
        throw new UnsupportedOperationException("SASL External not supported");

      case DIGEST_MD5:

        final DigestMd5Request digestMd5Request = ApacheLdapSaslUtil
          .createDigestMd5Request(
            request.getDn(),
            request.getCredential(),
            request.getSaslConfig());
        br = connection.bind(digestMd5Request);
        break;

      case CRAM_MD5:

        final CramMd5Request cramMd5Request = ApacheLdapSaslUtil
          .createCramMd5Request(
            request.getDn(),
            request.getCredential(),
            request.getSaslConfig());
        br = connection.bind(cramMd5Request);
        break;

      case GSSAPI:

        final GssApiRequest gssApiRequest = ApacheLdapSaslUtil
          .createGssApiRequest(
            request.getDn(),
            request.getCredential(),
            request.getSaslConfig());
        br = connection.bind(gssApiRequest);
        break;

      default:
        throw new IllegalArgumentException(
          "Unknown SASL authentication mechanism: " +
          request.getSaslConfig().getMechanism());
      }
      ApacheLdapUtil.throwOperationException(
        operationRetryResultCodes,
        br.getLdapResult().getResultCode());
      response = new Response<Void>(
        null,
        ResultCode.valueOf(br.getLdapResult().getResultCode().getResultCode()),
        ApacheLdapUtil.processResponseControls(
          controlProcessor,
          request.getControls(),
          br));
    } catch (LdapOperationException e) {
      ApacheLdapUtil.throwOperationException(operationRetryResultCodes, e);
    } catch (org.apache.directory.shared.ldap.model.exception.LdapException e) {
      throw new LdapException(e);
    } catch (IOException e) {
      throw new LdapException(e);
    }
    return response;
  }


  /** {@inheritDoc} */
  @Override
  public Response<Void> add(final AddRequest request)
    throws LdapException
  {
    Response<Void> response = null;
    try {
      final ApacheLdapUtil bu = new ApacheLdapUtil();
      final AddRequestImpl ari = new AddRequestImpl();
      if (request.getControls() != null) {
        ari.addAllControls(
          controlProcessor.processRequestControls(request.getControls()));
      }
      ari.setEntry(
        bu.fromLdapEntry(
          new LdapEntry(request.getDn(), request.getLdapAttributes())));

      final AddResponse ar = connection.add(ari);
      ApacheLdapUtil.throwOperationException(
        operationRetryResultCodes,
        ar.getLdapResult().getResultCode());
      response = new Response<Void>(
        null,
        ResultCode.valueOf(ar.getLdapResult().getResultCode().getResultCode()),
        ApacheLdapUtil.processResponseControls(
          controlProcessor,
          request.getControls(),
          ar));
    } catch (LdapOperationException e) {
      ApacheLdapUtil.throwOperationException(operationRetryResultCodes, e);
    } catch (org.apache.directory.shared.ldap.model.exception.LdapException e) {
      throw new LdapException(e);
    }
    return response;
  }


  /** {@inheritDoc} */
  @Override
  public Response<Boolean> compare(final CompareRequest request)
    throws LdapException
  {
    Response<Boolean> response = null;
    try {
      final CompareRequestImpl cri = new CompareRequestImpl();
      if (request.getControls() != null) {
        cri.addAllControls(
          controlProcessor.processRequestControls(request.getControls()));
      }
      cri.setName(new Dn(request.getDn()));
      cri.setAttributeId(request.getAttribute().getName());
      if (request.getAttribute().isBinary()) {
        cri.setAssertionValue(request.getAttribute().getBinaryValue());
      } else {
        cri.setAssertionValue(request.getAttribute().getStringValue());
      }

      final CompareResponse cr = connection.compare(cri);
      ApacheLdapUtil.throwOperationException(
        operationRetryResultCodes,
        cr.getLdapResult().getResultCode());
      response = new Response<Boolean>(
        cr.isTrue(),
        ResultCode.valueOf(cr.getLdapResult().getResultCode().getResultCode()),
        ApacheLdapUtil.processResponseControls(
          controlProcessor,
          request.getControls(),
          cr));
    } catch (LdapOperationException e) {
      ApacheLdapUtil.throwOperationException(operationRetryResultCodes, e);
    } catch (org.apache.directory.shared.ldap.model.exception.LdapException e) {
      throw new LdapException(e);
    }
    return response;
  }


  /** {@inheritDoc} */
  @Override
  public Response<Void> delete(final DeleteRequest request)
    throws LdapException
  {
    Response<Void> response = null;
    try {
      final DeleteRequestImpl dri = new DeleteRequestImpl();
      if (request.getControls() != null) {
        dri.addAllControls(
          controlProcessor.processRequestControls(request.getControls()));
      }
      dri.setName(new Dn(request.getDn()));

      final DeleteResponse dr = connection.delete(dri);
      ApacheLdapUtil.throwOperationException(
        operationRetryResultCodes,
        dr.getLdapResult().getResultCode());
      response = new Response<Void>(
        null,
        ResultCode.valueOf(dr.getLdapResult().getResultCode().getResultCode()),
        ApacheLdapUtil.processResponseControls(
          controlProcessor,
          request.getControls(),
          dr));
    } catch (LdapOperationException e) {
      ApacheLdapUtil.throwOperationException(operationRetryResultCodes, e);
    } catch (org.apache.directory.shared.ldap.model.exception.LdapException e) {
      throw new LdapException(e);
    }
    return response;
  }


  /** {@inheritDoc} */
  @Override
  public Response<Void> modify(final ModifyRequest request)
    throws LdapException
  {
    Response<Void> response = null;
    try {
      final ApacheLdapUtil bu = new ApacheLdapUtil();
      final ModifyRequestImpl mri = new ModifyRequestImpl();
      if (request.getControls() != null) {
        mri.addAllControls(
          controlProcessor.processRequestControls(request.getControls()));
      }
      mri.setName(new Dn(request.getDn()));
      for (
        Modification m :
        bu.fromAttributeModification(request.getAttributeModifications())) {
        mri.addModification(m);
      }

      final ModifyResponse mr = connection.modify(mri);
      ApacheLdapUtil.throwOperationException(
        operationRetryResultCodes,
        mr.getLdapResult().getResultCode());
      response = new Response<Void>(
        null,
        ResultCode.valueOf(mr.getLdapResult().getResultCode().getResultCode()),
        ApacheLdapUtil.processResponseControls(
          controlProcessor,
          request.getControls(),
          mr));
    } catch (LdapOperationException e) {
      ApacheLdapUtil.throwOperationException(operationRetryResultCodes, e);
    } catch (org.apache.directory.shared.ldap.model.exception.LdapException e) {
      throw new LdapException(e);
    }
    return response;
  }


  /** {@inheritDoc} */
  @Override
  public Response<Void> rename(final RenameRequest request)
    throws LdapException
  {
    Response<Void> response = null;
    try {
      final Dn dn = new Dn(request.getDn());
      final Dn newDn = new Dn(request.getNewDn());
      final ModifyDnRequestImpl mdri = new ModifyDnRequestImpl();
      if (request.getControls() != null) {
        mdri.addAllControls(
          controlProcessor.processRequestControls(request.getControls()));
      }
      mdri.setName(dn);
      mdri.setNewRdn(newDn.getRdn());
      mdri.setNewSuperior(newDn.getParent());
      mdri.setDeleteOldRdn(true);

      final ModifyDnResponse mdr = connection.modifyDn(mdri);
      ApacheLdapUtil.throwOperationException(
        operationRetryResultCodes,
        mdr.getLdapResult().getResultCode());
      response = new Response<Void>(
        null,
        ResultCode.valueOf(mdr.getLdapResult().getResultCode().getResultCode()),
        ApacheLdapUtil.processResponseControls(
          controlProcessor,
          request.getControls(),
          mdr));
    } catch (LdapOperationException e) {
      ApacheLdapUtil.throwOperationException(operationRetryResultCodes, e);
    } catch (org.apache.directory.shared.ldap.model.exception.LdapException e) {
      throw new LdapException(e);
    }
    return response;
  }


  /** {@inheritDoc} */
  @Override
  public SearchIterator search(
    final org.ldaptive.SearchRequest request)
    throws LdapException
  {
    final ApacheLdapSearchIterator i = new ApacheLdapSearchIterator(
      request,
      controlProcessor);
    i.setOperationRetryResultCodes(operationRetryResultCodes);
    i.initialize(connection);
    return i;
  }
}