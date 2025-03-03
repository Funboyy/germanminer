package de.germanminer.addon.v1_12_2;

import de.germanminer.addon.api.vehicle.Vehicle;
import de.germanminer.addon.controller.VehicleController;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.labymod.api.models.Implements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;

@Singleton
@Implements(VehicleController.class)
public class VersionedVehicleController extends VehicleController {

  @Inject
  public VersionedVehicleController() {
  }

  @Override
  public void fixVehicles(final List<Vehicle> vehicles) {
    final WorldClient world = Minecraft.getMinecraft().world;

    if (world == null) {
      return;
    }

    vehicles.forEach(vehicle -> {
      final Entity entity = world.getEntityByID(vehicle.getEntityId());

      if (entity != null) {
        EntityTracker.updateServerPosition(entity, vehicle.getX(), vehicle.getY(), vehicle.getZ());
        entity.setPositionAndRotationDirect(vehicle.getX(), vehicle.getY(), vehicle.getZ(),
            vehicle.getYaw(), vehicle.getPitch(), 3, true);
      }
    });
  }

}
