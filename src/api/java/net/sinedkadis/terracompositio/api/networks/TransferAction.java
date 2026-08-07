package net.sinedkadis.terracompositio.api.networks;

public enum TransferAction {
    EXECUTE, SIMULATE, BOTH;

    public boolean simulate() {
        return this == SIMULATE || this == BOTH;
    }

    public boolean execute() {
        return this == EXECUTE || this == BOTH;
    }
}
