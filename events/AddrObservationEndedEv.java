/*
#pragma ident "%W%      %E%     SMI"

 * Copyright (c) 1996 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies. Please refer to the file "copyright.html"
 * for further important copyright and licensing information.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */

package javax.telephony.events;

/**
 * The <CODE>AddrObservationEndedEv</CODE> event indicates that the application
 * will no longer receive Address events on the instance of the
 * <CODE>AddressObserver</CODE>. This interface extends the <CODE>AddrEv</CODE>
 * interface and is reported on the <CODE>AddressObserver</CODE> interface.
 * <p>
 * @see javax.telephony.events.AddrEv
 * @see javax.telephony.AddressObserver
 */

public interface AddrObservationEndedEv extends AddrEv {

  /**
   * Event id
   */
  public static final int ID = 100;
}
