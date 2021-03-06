/**
 * Copyright (c) 2017-2018 The Semux Developers
 *
 * Distributed under the MIT software license, see the accompanying file
 * LICENSE or https://opensource.org/licenses/mit-license.php
 */
package org.semux.core;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.semux.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionResultTest {

    private static final Logger logger = LoggerFactory.getLogger(TransactionResultTest.class);

    private byte[] returns = Bytes.random(20);
    private List<byte[]> logs = Arrays.asList(Bytes.random(8), Bytes.random(8));

    @Test
    public void testCode() {
        for (TransactionResult.Code code : TransactionResult.Code.values()) {
            if (code.name().startsWith("SUCCESS")) {
                assertTrue(code.isSuccess());
                assertFalse(code.isFailure());
                assertTrue(code.isAccepted());
                assertFalse(code.isRejected());
            } else if (code.name().startsWith("FAILURE")) {
                assertFalse(code.isSuccess());
                assertTrue(code.isFailure());
                assertTrue(code.isAccepted());
                assertFalse(code.isRejected());
            } else {
                assertFalse(code.isSuccess());
                assertFalse(code.isFailure());
                assertFalse(code.isAccepted());
                assertTrue(code.isRejected());
            }
        }
    }

    @Test
    public void testNew() {
        TransactionResult res = new TransactionResult(TransactionResult.Code.SUCCESS, returns, logs);
        testFields(res);
    }

    @Test
    public void testSerialization() {
        TransactionResult res = new TransactionResult(TransactionResult.Code.SUCCESS, returns, logs);

        testFields(TransactionResult.fromBytes(res.toBytes()));
    }

    @Test
    public void testTransactionResultSize() {
        TransactionResult res = new TransactionResult(TransactionResult.Code.SUCCESS, returns, logs);
        byte[] bytes = res.toBytes();

        logger.info("result size: {} B, {} GB per 1M txs", bytes.length, 1000000.0 * bytes.length / 1024 / 1024 / 1024);
    }

    private void testFields(TransactionResult res) {
        assertArrayEquals(returns, res.getReturnData());
        assertEquals(logs.size(), res.getLogs().size());
        for (int i = 0; i < logs.size(); i++) {
            assertArrayEquals(logs.get(i), res.getLogs().get(i));
        }
    }
}
