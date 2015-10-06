package mapleleafstrings.mapleleafapp;

/**
 * ============================= JSSEProvider.java =====================================
 *  Contains email protocol authentication stuff and the legal jargon for using it.
 *  Only conditions of use is that the legal comments are not removed from the source
 *  code, and that the software using the API probably shouldn't be patented (I'm not
 *  a lawyer).
 * ==================== Created by Christian Boler on 10/5/2015. =======================
 */

/*              --- TERMS OF USE: DO NOT REMOVE ---
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  The ASF licenses this file to You under
 *  the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import java.security.AccessController;
import java.security.Provider;

public final class JSSEProvider extends Provider {

    public JSSEProvider() {
        super("HarmonyJSSE", 1.0, "Harmony JSSE Provider");
        AccessController.doPrivileged(new java.security.PrivilegedAction<Void>() {
            public Void run() {
                put("SSLContext.TLS",
                        "org.apache.harmony.xnet.provider.jsse.SSLContextImpl");
                put("Alg.Alias.SSLContext.TLSv1", "TLS");
                put("KeyManagerFactory.X509",
                        "org.apache.harmony.xnet.provider.jsse.KeyManagerFactoryImpl");
                put("TrustManagerFactory.X509",
                        "org.apache.harmony.xnet.provider.jsse.TrustManagerFactoryImpl");
                return null;
            }
        });
    }
}
