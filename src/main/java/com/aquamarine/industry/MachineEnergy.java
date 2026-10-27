package com.aquamarine.industry;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import team.reborn.energy.api.base.SimpleEnergyStorage;
/** Energia interna consumida na mesma transação do produto; a rede só pode inserir. */
public class MachineEnergy extends SimpleEnergyStorage {
    private final Runnable dirty;
    public MachineEnergy(long capacity,long maxInsert,Runnable dirty) { super(capacity,maxInsert,0); this.dirty=dirty; }
    public boolean consume(long cost,TransactionContext tx) {
        if(cost<0 || amount<cost) return false;
        updateSnapshots(tx); amount-=cost; return true;
    }
    @Override protected void onFinalCommit() { dirty.run(); }
}
