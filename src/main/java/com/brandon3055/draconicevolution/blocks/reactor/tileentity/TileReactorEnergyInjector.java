package com.brandon3055.draconicevolution.blocks.reactor.tileentity;

import cofh.api.energy.IEnergyReceiver;
import net.minecraft.util.EnumFacing;

/**
 * Created by brandon3055 on 18/01/2017.
 */
public class TileReactorEnergyInjector extends TileReactorComponent implements IEnergyReceiver {

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        return super.receiveEnergy(from, maxReceive, simulate);
    }
}
