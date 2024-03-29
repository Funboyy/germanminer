package de.germanminer.addon;

import de.germanminer.addon.api.protocol.packet.GermanMinerPacket;
import de.germanminer.addon.api.protocol.packet.balance.BankBalancePacket;
import de.germanminer.addon.api.protocol.packet.balance.CashBalancePacket;
import de.germanminer.addon.api.protocol.packet.balance.CompanyBalancePacket;
import de.germanminer.addon.api.protocol.packet.balance.ExtraBalancePacket;
import de.germanminer.addon.api.protocol.packet.level.LevelPacket;
import de.germanminer.addon.api.protocol.packet.level.LevelPointsPacket;
import de.germanminer.addon.api.protocol.packet.miscellaneous.CompassPacket;
import de.germanminer.addon.api.protocol.packet.miscellaneous.InputPromptPacket;
import de.germanminer.addon.api.protocol.packet.miscellaneous.NotificationPacket;
import de.germanminer.addon.api.protocol.packet.miscellaneous.PowerUpPacket;
import de.germanminer.addon.api.protocol.packet.miscellaneous.VotePacket;
import de.germanminer.addon.api.protocol.packet.miscellaneous.ZonePacket;
import de.germanminer.addon.api.protocol.packet.playtime.DailyPlaytimePacket;
import de.germanminer.addon.api.protocol.packet.playtime.DutyPlaytimePacket;
import de.germanminer.addon.api.protocol.packet.playtime.PaydayPacket;
import de.germanminer.addon.api.protocol.packet.playtime.TotalPlaytimePacket;
import de.germanminer.addon.api.protocol.packet.playtime.WeeklyPlaytimePacket;
import de.germanminer.addon.api.protocol.packet.vehicle.VehicleDisplayPacket;
import de.germanminer.addon.api.protocol.packet.vehicle.VehiclePositionPacket;
import de.germanminer.addon.config.GermanMinerConfig;
import de.germanminer.addon.controller.HotKeyController;
import de.germanminer.addon.controller.VehicleController;
import de.germanminer.addon.core.generated.DefaultReferenceStorage;
import de.germanminer.addon.protocol.GermanMinerProtocol;
import de.germanminer.addon.protocol.handler.InputPromptPacketHandler;
import de.germanminer.addon.protocol.handler.NotificationPacketHandler;
import de.germanminer.addon.protocol.handler.VehiclePositionPacketHandler;
import de.germanminer.addon.protocol.translation.TranslationPacket;
import de.germanminer.addon.widgets.balance.BankBalanceWidget;
import de.germanminer.addon.widgets.balance.CashBalanceWidget;
import de.germanminer.addon.widgets.balance.CompanyBalanceWidget;
import de.germanminer.addon.widgets.balance.ExtraBalanceWidget;
import de.germanminer.addon.widgets.level.LevelPointsWidget;
import de.germanminer.addon.widgets.level.LevelWidget;
import de.germanminer.addon.widgets.miscellaneous.CalenderWidget;
import de.germanminer.addon.widgets.miscellaneous.CompassWidget;
import de.germanminer.addon.widgets.miscellaneous.PowerUpWidget;
import de.germanminer.addon.widgets.miscellaneous.VoteWidget;
import de.germanminer.addon.widgets.miscellaneous.ZoneWidget;
import de.germanminer.addon.widgets.playtime.DailyPlaytimeWidget;
import de.germanminer.addon.widgets.playtime.DutyPlaytimeWidget;
import de.germanminer.addon.widgets.playtime.PaydayWidget;
import de.germanminer.addon.widgets.playtime.TotalPlaytimeWidget;
import de.germanminer.addon.widgets.playtime.WeeklyPlaytimeWidget;
import de.germanminer.addon.widgets.vehicle.VehicleDisplayWidget;
import java.util.Arrays;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.client.gui.hud.HudWidgetRegistry;
import net.labymod.api.client.gui.hud.binding.category.HudWidgetCategory;
import net.labymod.api.models.addon.annotation.AddonMain;
import net.labymod.serverapi.protocol.api.ProtocolApiBridge;
import net.labymod.serverapi.protocol.packet.protocol.Protocol;
import net.labymod.serverapi.protocol.packet.protocol.ProtocolService;
import org.jetbrains.annotations.Nullable;

@AddonMain
public class GermanMinerAddon extends LabyAddon<GermanMinerConfig> {

  private static GermanMinerAddon instance;
  private boolean online = false;
  private Protocol protocol;
  private HudWidgetCategory category;
  private VehicleDisplayWidget vehicleWidget;

  public static GermanMinerAddon getInstance() {
    return instance;
  }

  public static int getVersion() {
    return 2;
  }

  @Override
  protected void enable() {
    this.registerSettingCategory();

    this.logger().info("[GermanMiner] Initialising...");

    instance = this;
    this.labyAPI().serverController().registerServer(new GermanMinerServer(this));

    this.logger().info("[GermanMiner] Registering Protocol...");

    this.protocol = new GermanMinerProtocol();
    final ProtocolService protocolService = ProtocolApiBridge.getProtocolApi().getProtocolService();
    protocolService.registerCustomProtocol(this.protocol);

    Arrays.stream(TranslationPacket.values()).forEach(translation -> translation.register(protocolService));

    this.logger().info("[GermanMiner] Registering Widgets...");

    final HudWidgetRegistry registry = this.labyAPI().hudWidgetRegistry();
    this.category = new HudWidgetCategory(this, "gmGlobal");
    registry.categoryRegistry().register(this.category);

    new CashBalanceWidget(this, "gmCash").register(registry, protocolService, CashBalancePacket.class);
    new BankBalanceWidget(this, "gmBank").register(registry, protocolService, BankBalancePacket.class);
    new CompanyBalanceWidget(this, "gmCompany").register(registry, protocolService, CompanyBalancePacket.class);
    new ExtraBalanceWidget(this, "gmExtra").register(registry, protocolService, ExtraBalancePacket.class);

    new LevelWidget(this, "gmLevel").register(registry, protocolService, LevelPacket.class);
    new LevelPointsWidget(this, "gmLevelPoints").register(registry, protocolService, LevelPointsPacket.class);

    new DailyPlaytimeWidget(this, "gmPlayTimeDaily").register(registry, protocolService, DailyPlaytimePacket.class);
    new WeeklyPlaytimeWidget(this, "gmPlayTimeWeekly").register(registry, protocolService, WeeklyPlaytimePacket.class);
    new TotalPlaytimeWidget(this, "gmPlayTimeTotal").register(registry, protocolService, TotalPlaytimePacket.class);
    new DutyPlaytimeWidget(this, "gmPlayTimeDuty").register(registry, protocolService, DutyPlaytimePacket.class);
    new PaydayWidget(this, "gmPayday").register(registry, protocolService, PaydayPacket.class);

    new CompassWidget(this, "gmCompass").register(registry, protocolService, CompassPacket.class);
    new PowerUpWidget(this, "gmPowerUp").register(registry, protocolService, PowerUpPacket.class);
    new VoteWidget(this, "gmVote").register(registry, protocolService, VotePacket.class);
    new ZoneWidget(this, "gmZone").register(registry, protocolService, ZonePacket.class);

    registry.register(new CalenderWidget(this, "gmCalender"));

    this.vehicleWidget = new VehicleDisplayWidget(this, "gmVehicle");
    this.vehicleWidget.register(registry, protocolService, VehicleDisplayPacket.class);

    this.logger().info("[GermanMiner] Registering Features...");

    protocolService.registerPacketHandler(NotificationPacket.class, new NotificationPacketHandler());
    protocolService.registerPacketHandler(VehiclePositionPacket.class, new VehiclePositionPacketHandler());
    protocolService.registerPacketHandler(InputPromptPacket.class, new InputPromptPacketHandler());

    new HotKeyController(this);

    this.logger().info("[GermanMiner] Finished initialization.");
  }

  @Override
  protected Class<GermanMinerConfig> configurationClass() {
    return GermanMinerConfig.class;
  }

  public void sendPacket(final GermanMinerPacket packet) {
    this.protocol.sendPacket(packet);
  }

  public boolean enabled() {
    return this.online && this.configuration().enabled().get();
  }

  public void setOnline(final boolean online) {
    this.online = online;
  }

  public Protocol getProtocol() {
    return this.protocol;
  }

  public HudWidgetCategory getCategory() {
    return this.category;
  }

  public VehicleDisplayWidget getVehicleWidget() {
    return this.vehicleWidget;
  }

  @Nullable
  public VehicleController getVehicleController() {
    final DefaultReferenceStorage references = this.referenceStorageAccessor();
    return references.getVehicleController();
  }

}
