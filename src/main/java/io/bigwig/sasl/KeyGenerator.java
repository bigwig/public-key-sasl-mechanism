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

import org.bouncycastle.openssl.PEMWriter;

import java.io.StringWriter;
import java.security.*;

public class KeyGenerator {

  public static void main(String[] args) throws Exception {

    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

    KeyPairGenerator generator = KeyPairGenerator.getInstance("DSA", "BC");

    SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
    generator.initialize(1024, random);

    KeyPair pair = generator.generateKeyPair();
    PublicKey publicKey = pair.getPublic();
    PrivateKey privateKey = pair.getPrivate();

    System.out.println(toPemFormat(publicKey));
    System.out.println(toPemFormat(privateKey));

  }

  public static String toPemFormat(Key key) throws Exception{
    StringWriter sw = new StringWriter();
    PEMWriter pemFormatWriter = new PEMWriter(sw);
    pemFormatWriter.writeObject(key);
    pemFormatWriter.close();
    return sw.toString();
  }
}
