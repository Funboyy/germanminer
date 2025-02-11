package de.germanminer.addon.v1_21_3;

import de.germanminer.addon.api.vehicle.Vehicle;
import de.germanminer.addon.controller.VehicleController;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.labymod.api.models.Implements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;

@Singleton
@Implements(VehicleController.class)
public class VersionedVehicleController extends VehicleController {

  @Inject
  public VersionedVehicleController() {
  }

  @Override
  public void fixVehicles(final List<Vehicle> vehicles) {
    final ClientLevel level = Minecraft.getInstance().level;

    if (level == null) {
      return;
    }

    vehicles.forEach(vehicle -> {
      final Entity entity = level.getEntity(vehicle.getEntityId());

      if (entity != null) {
        entity.syncPacketPositionCodec(vehicle.getX(), vehicle.getY(), vehicle.getZ());
        entity.lerpTo(vehicle.getX(), vehicle.getY(), vehicle.getZ(),
            vehicle.getYaw(), vehicle.getPitch(), 3);
      }
    });
  }

}
