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

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.junit.Test;

import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.assertTrue;

public class PublicKeyITest {

  @Test
  public void testDefaultUser() throws Exception {

    // Read in a private key from the file system and pass it to the SASL configuration

    InputStream is = getClass().getClassLoader().getResourceAsStream("test_dsa.pem");
    PublicKeyConfig saslConfig = new PublicKeyConfig(new InputStreamReader(is));

    ConnectionFactory factory = new ConnectionFactory();
    factory.setSaslConfig(saslConfig);

    // Override any other non-default parameters for connecting to Bigwig, for example:
    // factory.setUsername("gljJvcFZwoMW9");
    // factory.setPort(10160);
    // factory.setVirtualHost("B6F90F1uhpNv");

    Connection connection = factory.newConnection();

    assertTrue("Could not open connection to Bigwig using public key authentication", connection.isOpen());

    connection.close();
  }
}
