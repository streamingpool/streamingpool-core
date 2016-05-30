/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package demo.usecase.bis;

import stream.StreamId;

enum RedundantPermitId implements StreamId<UserPermit>{
    USER_PERMIT_1_A(0),
    USER_PERMIT_1_B(16),
    USER_PERMIT_2_A(1),
    USER_PERMIT_2_B(17),
    USER_PERMIT_3_A(2),
    USER_PERMIT_3_B(18),
    USER_PERMIT_4_A(3),
    USER_PERMIT_4_B(19),
    USER_PERMIT_5_A(4),
    USER_PERMIT_5_B(20),
    USER_PERMIT_6_A(5),
    USER_PERMIT_6_B(21),
    USER_PERMIT_7_A(6),
    USER_PERMIT_7_B(22),
    USER_PERMIT_8_A(7),
    USER_PERMIT_8_B(23),
    USER_PERMIT_9_A(8),
    USER_PERMIT_9_B(24),
    USER_PERMIT_10_A(9),
    USER_PERMIT_10_B(25),
    USER_PERMIT_11_A(10),
    USER_PERMIT_11_B(26),
    USER_PERMIT_12_A(11),
    USER_PERMIT_12_B(27),
    USER_PERMIT_13_A(12),
    USER_PERMIT_13_B(28),
    USER_PERMIT_14_A(13),
    USER_PERMIT_14_B(29),
    USER_PERMIT_15_A(14),
    USER_PERMIT_15_B(30),
    USER_PERMIT_16_A(15),
    USER_PERMIT_16_B(31);

    int offset;

    private RedundantPermitId(int offset) {
        this.offset = offset;
    }

}