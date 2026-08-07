package net.sinedkadis.terracompositio.api.helpers;

import net.sinedkadis.terracompositio.api.TerraCompositioAPI;
import net.sinedkadis.terracompositio.api.networks.ecf.ECFNetworkMember;

import java.util.function.IntBinaryOperator;

/**
 * The class with methods, that helps with ECF.
 */
public class ECFHelper {

    /**
     * New transfer builder to make transfer.
     *
     * @return the builder
     */
    public static ECFTransferBuilder newTransfer() {
        return new ECFTransferBuilder();
    }

    /**
     * Convenient builder for ECF transfers.
     */
    public static class ECFTransferBuilder {
        /**
         * The Target member.
         */
        ECFNetworkMember target = null;
        /**
         * The Source member.
         */
        ECFNetworkMember source = null;

//        /**
//         * The max amount of ECF that can be transferred.
//         */
//        int maxTransfer = TerraCompositioAPI.instance().getECFNetworkInstance().getECFTransferLimit();
        /**
         * The Speed. Default to 1 block per second.
         */
        float speed = 1 / 20f;


        /**
         * Marks transfer to call
         * {@link net.sinedkadis.terracompositio.api.networks.ecf.ECFNetwork#validateRelation(ECFNetworkMember, ECFNetworkMember, IntBinaryOperator)}
         * , defaults to true
         */
        boolean validate = true;


        /**
         * Target and source for transfer. Mandatory.
         *
         * @param target the target
         * @param source the source
         * @return the ecf transfer builder
         */
        public ECFTransferBuilder targetAndSource(ECFNetworkMember target, ECFNetworkMember source) {
            this.target = target;
            this.source = source;
            return this;
        }
//
//        /**
//         * Max transfer amount. Optional.
//         *
//         * @param maxTransfer the max transfer
//         * @return the ecf transfer builder
//         */
//        public ECFTransferBuilder maxTransfer(int maxTransfer) {
//            this.maxTransfer = maxTransfer;
//            return this;
//        }

        /**
         * Speed. Optional.
         *
         * @param speed the speed
         * @return the ecf transfer builder
         */
        public ECFTransferBuilder speed(float speed) {
            this.speed = speed;
            return this;
        }

        /**
         * Disables transfer validation
         */
        public ECFTransferBuilder noValidate() {
            this.validate = false;
            return this;
        }

        /**
         * Executes transfer with given in builder data.
         */
        public void build() {
            if (target != null && source != null) {
                TerraCompositioAPI.instance().getECFNetworkInstance().executeECFTransfer(target, source, speed, validate);
            }
        }

    }
}
