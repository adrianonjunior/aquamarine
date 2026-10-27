package com.aquamarine.gametest;

import com.aquamarine.industry.*;
import com.aquamarine.farming.*;
import com.aquamarine.entity.*;
import com.aquamarine.item.ModItems;
import com.aquamarine.network.ModNetworks;
import java.util.Locale;
import net.fabricmc.fabric.api.client.gametest.v1.context.*;
import net.fabricmc.fabric.api.transfer.v1.fluid.*;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.*;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;

/** Onda 3: conservação transacional, coluna/persistência, bombeamento real, cultivo e fauna. */
final class IndustryWave3GameTest {
    static final long B=FluidConstants.BUCKET;
    static void check(boolean ok,String why) { if(!ok) throw new AssertionError(why); }
    static HydraulicBlockEntity tank(ServerLevel l,BlockPos p) { return (HydraulicBlockEntity)l.getBlockEntity(p); }
    static void place(ServerLevel l,BlockPos p,Block b) { l.setBlockAndUpdate(p,b.defaultBlockState()); }
    static void reload(ServerLevel l,BlockPos p) {
        var old=l.getBlockEntity(p);
        var tag=old.saveWithFullMetadata(l.registryAccess());
        var restored=BlockEntity.loadStatic(p,l.getBlockState(p),tag,l.registryAccess());
        check(restored!=null,"Persistência: block entity não recarregou");
        l.removeBlockEntity(p); l.setBlockEntity(restored);
    }
    static void run(ClientGameTestContext c,TestSingleplayerContext world,TestServerContext server,BlockPos base) {
        BlockPos o=base.offset(-55,0,-75), stack=o.offset(0,0,5), pump=o.offset(0,0,0), dest=o.offset(3,0,0), jar=o.offset(8,0,5), crabPos=o.offset(2,0,13);
        server.runCommand(String.format("forceload add %d %d %d %d",o.getX()-5,o.getZ()-3,o.getX()+17,o.getZ()+18));
        server.runOnServer(s->{
            var l=s.overworld();
            s.getPlayerList().getPlayers().getFirst().setGameMode(GameType.SPECTATOR);
            for(BlockPos p:BlockPos.betweenClosed(o.offset(-5,-1,-3),o.offset(17,-1,18))) place(l,p,Blocks.SMOOTH_STONE);
            for(BlockPos p:BlockPos.betweenClosed(o.offset(-5,0,-3),o.offset(17,5,18))) place(l,p,Blocks.AIR);
            place(l,stack,ModHydraulics.WATER_TANK); place(l,stack.above(),ModHydraulics.WATER_TANK);
            var t=tank(l,stack); var water=FluidVariant.of(Fluids.WATER);
            try(var tx=Transaction.openOuter()) { check(t.inventory().insert(water,100*B,tx)==100*B,"Coluna: deve aceitar 100 baldes"); }
            check(t.totalAmount()==0,"Transação abortada duplicou água");
            try(var tx=Transaction.openOuter()) { check(t.inventory().insert(water,150*B,tx)==128*B,"Coluna: capacidade deve ser 128"); tx.commit(); }
            check(t.amount()==64*B && tank(l,stack.above()).amount()==64*B,"Cada tanque deve guardar 64 baldes");
            try(var tx=Transaction.openOuter()) { check(t.inventory().insert(FluidVariant.of(Fluids.LAVA),B,tx)==0,"Não pode misturar lava"); }
            reload(l,stack); reload(l,stack.above());
            check(tank(l,stack).totalAmount()==128*B,"Save perdeu conteúdo da coluna");
            var port=tank(l,stack).fluidPort(Direction.NORTH);
            tank(l,stack).cyclePort(Direction.NORTH); // IN -> OFF
            try(var tx=Transaction.openOuter()) { check(port.insert(water,B,tx)==0 && port.extract(water,B,tx)==0,"Porta guardada ignorou desligamento"); }
            place(l,stack.above(),Blocks.AIR);
            check(tank(l,stack).totalAmount()==64*B,"Remover metade da coluna alterou tanque restante");
            try(var tx=Transaction.openOuter()) { tank(l,stack).inventory().extract(water,32*B,tx); tx.commit(); }
            // Circuito Bomba -> Cano -> Tanque, captando de fonte renovável real.
            for(BlockPos p:BlockPos.betweenClosed(pump.offset(-3,0,-1),pump.offset(-1,0,1))) place(l,p,Blocks.WATER);
            for(BlockPos p:BlockPos.betweenClosed(pump.offset(-4,0,-2),pump.offset(0,0,2)))
                if(p.getX()==pump.getX()-4 || p.getZ()==pump.getZ()-2 || p.getZ()==pump.getZ()+2 || p.getX()==pump.getX()) place(l,p,Blocks.GLASS);
            place(l,pump,ModHydraulics.TIDE_PUMP); tank(l,pump).energy.amount=16000;
            place(l,pump.east(),ModNetworks.FLUID_PIPE); place(l,pump.east(2),ModNetworks.FLUID_PIPE); place(l,dest,ModHydraulics.WATER_TANK);
            // Duas garrafas e uma boia separadas por cordas, na água limpa.
            for(BlockPos p:BlockPos.betweenClosed(jar.offset(-1,0,-1),jar.offset(5,2,1))) place(l,p,Blocks.WATER);
            for(BlockPos p:BlockPos.betweenClosed(jar.offset(-2,0,-2),jar.offset(6,3,2)))
                if(p.getX()==jar.getX()-2 || p.getX()==jar.getX()+6 || p.getZ()==jar.getZ()-2 || p.getZ()==jar.getZ()+2) place(l,p,Blocks.GLASS);
            Block[] line={ModCultivation.CULTIVATION_JAR,ModCultivation.CULTIVATION_ROPE,ModCultivation.CULTIVATION_JAR,ModCultivation.CULTIVATION_ROPE,ModCultivation.CULTIVATION_BUOY};
            for(int i=0;i<line.length;i++) l.setBlockAndUpdate(jar.east(i),line[i].defaultBlockState().setValue(com.aquamarine.block.Waterlogging.WATERLOGGED,true));
            var j=(CultivationJarBlockEntity)l.getBlockEntity(jar); j.setProgressUnits(CultivationJarBlockEntity.CYCLE-11);
            reload(l,jar); check(((CultivationJarBlockEntity)l.getBlockEntity(jar)).progressUnits()==CultivationJarBlockEntity.CYCLE-11,"Cultivo perdeu progresso ao recarregar");
            var oyster=ModCultivation.OYSTER.create(l,EntitySpawnReason.COMMAND); oyster.setPos(jar.getX()-.3,jar.getY(),jar.getZ()+.5); oyster.setPersistenceRequired(); l.addFreshEntity(oyster);
            for(BlockPos p:BlockPos.betweenClosed(crabPos.offset(-3,-1,-3),crabPos.offset(3,-1,3))) place(l,p,Blocks.SAND);
            var crab=ModEntities.CRAB.create(l,EntitySpawnReason.COMMAND); crab.setPos(crabPos.getX()+.5,crabPos.getY(),crabPos.getZ()+.5); crab.setPersistenceRequired(); l.addFreshEntity(crab);
            check(new ItemStack(ModItems.SHELL_SHIELD).getMaxDamage()==672,"Escudo: durabilidade incorreta");
        });
        look(server,o.offset(3,0,2),5,3,-7); c.waitTicks(65);
        server.runOnServer(s->{
            var l=s.overworld();
            check(tank(l,dest).amount()>0,"Bomba não abasteceu Tanque pelo Cano");
            check(tank(l,pump).energy.amount<16000,"Bomba não consumiu energia");
            check(l.getFluidState(pump.west()).isSourceOfType(Fluids.WATER),"Bomba esgotou fonte renovável");
            var j=(CultivationJarBlockEntity)l.getBlockEntity(jar);
            check(j.working() && j.pearls()==1,"Linha ligada não produziu pérola"); reload(l,jar);
            check(((CultivationJarBlockEntity)l.getBlockEntity(jar)).pearls()==1,"Pérola desapareceu ao recarregar");
        });
        shot(c,world,server,o.offset(1,0,0),"onda3_hidraulica_run",3,2.7,-4);
        shot(c,world,server,stack,"onda3_tanque_meio",1.8,1.9,-2.5);
        check(c.computeOnClient(mc->mc.level.getBlockEntity(stack) instanceof HydraulicBlockEntity b && b.amount()==32*B && b.fill(1)>.45F),"Cliente não recebeu nível de meio tanque");
        shot(c,world,server,jar.east(2),"onda3_cultivo",3,2,-4);
        shot(c,world,server,crabPos,"onda3_caranguejo",1.8,1.4,-2.1);
        shot(c,world,server,jar.west(),"onda3_ostra",-.8,.85,-1.4);
        server.runOnServer(s->{
            try(var tx=Transaction.openOuter()) { tank(s.overworld(),stack).inventory().insert(FluidVariant.of(Fluids.WATER),32*B,tx); tx.commit(); }
        });
        c.waitTicks(25);
        shot(c,world,server,stack,"onda3_tanque_cheio",1.8,1.9,-2.5);
        check(c.computeOnClient(mc->mc.level.getBlockEntity(stack) instanceof HydraulicBlockEntity b && b.amount()==64*B && b.fill(1)>.95F),"Cliente não recebeu nível de tanque cheio");
        server.runOnServer(s->{
            var l=s.overworld(); var j=(CultivationJarBlockEntity)l.getBlockEntity(jar);
            check(j.takePearls()==1 && j.takePearls()==0,"Coleta duplicou pérola");
            place(l,jar.east(),Blocks.WATER);
            CultivationJarBlockEntity.tick(l,jar,l.getBlockState(jar),j);
            check(!j.working(),"Corda quebrada manteve produção");
            l.setBlockAndUpdate(jar.east(),ModCultivation.CULTIVATION_ROPE.defaultBlockState().setValue(com.aquamarine.block.Waterlogging.WATERLOGGED,true));
            place(l,jar.below(),Blocks.MAGMA_BLOCK);
            CultivationJarBlockEntity.tick(l,jar,l.getBlockState(jar),j); check(!j.working(),"Água com magma permitiu cultivo");
            var crab=l.getEntitiesOfClass(CrabEntity.class,new AABB(crabPos).inflate(10)).getFirst();
            crab.setPos(crabPos.getX()+.5,crabPos.getY(),crabPos.getZ()+.5); crab.getNavigation().stop(); crab.setDeltaMovement(0,0,0);
            var p=s.getPlayerList().getPlayers().getFirst(); p.setGameMode(GameType.SURVIVAL);
        });
        server.runCommand(String.format(Locale.ROOT,"tp @p %.2f %d %.2f",crabPos.getX()+1.3,crabPos.getY(),crabPos.getZ()+.5)); c.waitTicks(45);
        server.runOnServer(s->{
            var l=s.overworld(); var crab=l.getEntitiesOfClass(CrabEntity.class,new AABB(crabPos).inflate(5)).getFirst();
            check(crab.activity()==CrabEntity.Activity.HIDDEN && crab.isInvisible(),"Caranguejo ameaçado não se escondeu na areia");
            s.getPlayerList().getPlayers().getFirst().setGameMode(GameType.CREATIVE);
        });
        c.waitTicks(90);
        server.runOnServer(s->{
            var l=s.overworld(); var crab=l.getEntitiesOfClass(CrabEntity.class,new AABB(crabPos).inflate(6)).getFirst();
            check(!crab.isInvisible(),"Caranguejo não saiu da toca após ameaça passar");
        });
        // A persistência acima usa a serialização completa e uma nova instância, nunca só o mesmo objeto.
    }
    static void look(TestServerContext s,BlockPos p,double dx,double dy,double dz) {
        double yaw=Math.toDegrees(Math.atan2(dx,-dz)),pitch=Math.toDegrees(Math.atan2(dy-.5,Math.sqrt(dx*dx+dz*dz)));
        s.runCommand(String.format(Locale.ROOT,"tp @p %.2f %.2f %.2f %.2f %.2f",p.getX()+.5+dx,p.getY()+dy-1.62,p.getZ()+.5+dz,yaw,pitch));
    }
    static void shot(ClientGameTestContext c,TestSingleplayerContext w,TestServerContext s,BlockPos p,String name,double dx,double dy,double dz) {
        look(s,p,dx,dy,dz); w.getConnection().waitForChunksRender(); c.waitTicks(12);
        c.runOnClient(mc->mc.gui.hud.getChat().clearMessages(false)); c.takeScreenshot(name);
    }
}
