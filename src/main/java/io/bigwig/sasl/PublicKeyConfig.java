//  The contents of this file are subject to the Mozilla Public License
//  Version 1.1 (the "License"); you may not use this file except in
//  compliance with the License. You may obtain a copy of the License
//  at http://www.mozilla.org/MPL/
//
//  Software distributed under the License is distributed on an "AS IS"
//  basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
//  the License for the specific language governing rights and
//  limitations under the License.
//
//  The Original Code is LShift Ltd.
//
//  The Initial Developer of the Original Code is LShift Ltd.
//  Copyright (c) 2013 LShift Ltd.  All rights reserved.

package io.bigwig.sasl;

import com.rabbitmq.client.SaslConfig;
import com.rabbitmq.client.SaslMechanism;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.openssl.PEMReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Security;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PublicKeyConfig implements SaslConfig {

  static {
    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
  }

  static Logger log = LoggerFactory.getLogger(PublicKeyConfig.class);

  private PrivateKey privateKey;

  public PublicKeyConfig(Reader reader) {
    PEMReader pemReader = new PEMReader(reader);
    KeyPair keyPair = null;

    try {

      Object parsed = pemReader.readObject();

      if (parsed instanceof KeyPair) {
        keyPair = (KeyPair) pemReader.readObject();
      }
      else if (parsed instanceof ECNamedCurveParameterSpec) {
        ECNamedCurveParameterSpec curveSpec = (ECNamedCurveParameterSpec) parsed;
        keyPair = (KeyPair) pemReader.readObject();
      }

    } catch (IOException e) {
      log.error("Could not read private key from source reader");
      throw new RuntimeException(e);
    }

    privateKey = keyPair.getPrivate();
  }

  @Override
  public SaslMechanism getSaslMechanism(String[] mechanisms) {
    Set<String> serverMechanism = new HashSet<String>(Arrays.asList(mechanisms));

    if (serverMechanism.contains("PUBLIC-KEY")) {
      return new PublicKeyMechanism(privateKey);
    }
    return null;
  }

}
