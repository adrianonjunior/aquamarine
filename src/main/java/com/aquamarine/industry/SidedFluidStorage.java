package com.aquamarine.industry;

import java.util.Iterator;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

/** Fachada viva: mudanças de lado e da coluna valem também para referências guardadas por outros mods. */
public final class SidedFluidStorage implements Storage<FluidVariant> {
    private final Supplier<Storage<FluidVariant>> storage;
    private final BooleanSupplier insert, extract;
    public SidedFluidStorage(Supplier<Storage<FluidVariant>> storage, BooleanSupplier insert, BooleanSupplier extract) {
        this.storage = storage; this.insert = insert; this.extract = extract;
    }
    @Override public boolean supportsInsertion() { return insert.getAsBoolean(); }
    @Override public boolean supportsExtraction() { return extract.getAsBoolean(); }
    @Override public long insert(FluidVariant v, long max, TransactionContext tx) {
        return supportsInsertion() ? storage.get().insert(v, max, tx) : 0;
    }
    @Override public long extract(FluidVariant v, long max, TransactionContext tx) {
        return supportsExtraction() ? storage.get().extract(v, max, tx) : 0;
    }
    @Override public Iterator<StorageView<FluidVariant>> iterator() {
        Iterator<StorageView<FluidVariant>> delegate = storage.get().iterator();
        return new Iterator<>() {
            public boolean hasNext() { return delegate.hasNext(); }
            public StorageView<FluidVariant> next() {
                StorageView<FluidVariant> view = delegate.next();
                return new StorageView<>() {
                    public long extract(FluidVariant v, long max, TransactionContext tx) {
                        return supportsExtraction() ? view.extract(v, max, tx) : 0;
                    }
                    public boolean isResourceBlank() { return view.isResourceBlank(); }
                    public FluidVariant getResource() { return view.getResource(); }
                    public long getAmount() { return view.getAmount(); }
                    public long getCapacity() { return view.getCapacity(); }
                };
            }
        };
    }
}

