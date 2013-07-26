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

import com.rabbitmq.client.LongString;
import com.rabbitmq.client.SaslMechanism;
import com.rabbitmq.client.impl.LongStringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Encoder;

import java.security.*;

public class PublicKeyMechanism implements SaslMechanism {

  static BASE64Encoder encoder = new BASE64Encoder();
  static Logger log = LoggerFactory.getLogger(PublicKeyMechanism.class);

  final private PrivateKey privateKey;

  public PublicKeyMechanism(PrivateKey privateKey) {
    this.privateKey = privateKey;
  }

  @Override
  public String getName() {
    return "PUBLIC-KEY";
  }

  @Override
  public LongString handleChallenge(LongString challenge, String username, String password) {

    if (challenge == null) {
      return LongStringHelper.asLongString(username);
    } else {

      Signature signer;

      try {

        signer = Signature.getInstance("DSA", "BC");

      } catch (NoSuchAlgorithmException e) {
        log.error("Could not load DSA algorithm");
        throw new RuntimeException(e);
      } catch (NoSuchProviderException e) {
        log.error("Could load Bouncy Castle provider");
        throw new RuntimeException(e);
      }

      try {

        signer.initSign(privateKey);

      } catch (InvalidKeyException e) {
        log.error("Private key was invalid: {}", e.getMessage());
        throw new RuntimeException(e);
      }

      try {

        signer.update(challenge.getBytes());
        byte[] sig = signer.sign();

        log.debug("Signature: {}", encoder.encode(sig));

        return LongStringHelper.asLongString(sig);

      } catch (SignatureException e) {
        log.error("Failed to sign nonce: {} ", e.getMessage());
        throw new RuntimeException(e);
      }

    }
  }
}
