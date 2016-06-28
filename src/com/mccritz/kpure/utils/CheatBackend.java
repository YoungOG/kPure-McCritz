package com.mccritz.kpure.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.potion.PotionEffectType;

import com.mccritz.kpure.kPure;

public class CheatBackend {

    private List<String> isInWater = new ArrayList<>();
    private List<String> isInWaterCache = new ArrayList<>();
    private List<String> isAscending = new ArrayList<>();
    private Map<String, Integer> interactionCount = new HashMap<>();
    private Map<String, Integer> ascensionCount = new HashMap<>();
    private Map<String, Double> blocksOverFlight = new HashMap<>();
    private Map<String, Integer> nofallViolation = new HashMap<>();
    private Map<String, Integer> speedViolation = new HashMap<>();
    private Map<String, Integer> fastBreakViolation = new HashMap<>();
    private Map<String, Integer> yAxisViolations = new HashMap<>();
    private Map<String, Long> yAxisLastViolation = new HashMap<>();
    private Map<String, Double> lastYcoord = new HashMap<>();
    private Map<String, Long> lastYtime = new HashMap<>();
    private Map<String, Integer> fastBreaks = new HashMap<>();
    private Map<String, Boolean> blockBreakHolder = new HashMap<>();
    private Map<String, Long> lastBlockBroken = new HashMap<>();
    private Map<String, Integer> fastPlaceViolation = new HashMap<>();
    private Map<String, Long> lastBlockPlaced = new HashMap<>();
    private Map<String, Long> lastBlockPlaceTime = new HashMap<>();
    private Map<String, Integer> blockPunches = new HashMap<>();
    private Map<String, Integer> waterAscensionViolation = new HashMap<>();
    private Map<String, Integer> waterSpeedViolation = new HashMap<>();
    private Map<String, Integer> projectilesShot = new HashMap<>();
    private Map<String, Long> velocitized = new HashMap<>();
    private Map<String, Integer> velocitytrack = new HashMap<>();
    private Map<String, Long> animated = new HashMap<>();
    private Map<String, Long> startEat = new HashMap<>();
    private Map<String, Long> lastHeal = new HashMap<>();
    private Map<String, Long> projectileTime = new HashMap<>();
    private Map<String, Long> bowWindUp = new HashMap<>();
    private Map<String, Long> instantBreakExempt = new HashMap<>();
    private Map<String, Long> sprinted = new HashMap<>();
    private Map<String, Long> brokenBlock = new HashMap<>();
    private Map<String, Long> placedBlock = new HashMap<>();
    private Map<String, Long> movingExempt = new HashMap<>();
    private Map<String, Long> blockTime = new HashMap<>();
    private Map<String, Integer> blocksDropped = new HashMap<>();
    private Map<String, Long> inventoryTime = new HashMap<>();
    private Map<String, Integer> inventoryClicks = new HashMap<>();
    private Map<String, Material> itemInHand = new HashMap<>();
    private Map<String, Integer> steps = new HashMap<>();
    private Map<String, Long> stepTime = new HashMap<>();

    public void garbageClean(Player player) {
	String pN = player.getName();

	blocksDropped.remove(pN);
	blockTime.remove(pN);
	movingExempt.remove(pN);
	brokenBlock.remove(pN);
	placedBlock.remove(pN);
	bowWindUp.remove(pN);
	startEat.remove(pN);
	lastHeal.remove(pN);
	sprinted.remove(pN);
	isInWater.remove(pN);
	isInWaterCache.remove(pN);
	instantBreakExempt.remove(pN);
	isAscending.remove(pN);
	ascensionCount.remove(pN);
	blocksOverFlight.remove(pN);
	nofallViolation.remove(pN);
	fastBreakViolation.remove(pN);
	yAxisViolations.remove(pN);
	yAxisLastViolation.remove(pN);
	lastYcoord.remove(pN);
	lastYtime.remove(pN);
	fastBreaks.remove(pN);
	blockBreakHolder.remove(pN);
	lastBlockBroken.remove(pN);
	fastPlaceViolation.remove(pN);
	lastBlockPlaced.remove(pN);
	lastBlockPlaceTime.remove(pN);
	blockPunches.remove(pN);
	waterAscensionViolation.remove(pN);
	waterSpeedViolation.remove(pN);
	projectilesShot.remove(pN);
	velocitized.remove(pN);
	velocitytrack.remove(pN);
	animated.remove(pN);
	startEat.remove(pN);
	lastHeal.remove(pN);
	projectileTime.remove(pN);
	bowWindUp.remove(pN);
	instantBreakExempt.remove(pN);
	sprinted.remove(pN);
	brokenBlock.remove(pN);
	placedBlock.remove(pN);
	movingExempt.remove(pN);
	blockTime.remove(pN);
	blocksDropped.remove(pN);
	inventoryTime.remove(pN);
	inventoryClicks.remove(pN);
    }

    public boolean checkFastBow(Player player, float force) {
	int ticks = (int) ((System.currentTimeMillis() - bowWindUp.get(player.getName())) * 20 / 1000 + 3);
	bowWindUp.remove(player.getName());
	float f = ticks / 20.0F;
	f = (f * f + f * 2.0F) / 3.0F;
	f = f > 1.0F ? 1.0F : f;

	return Math.abs(force - f) > 0.25;
    }

    public boolean checkProjectile(Player player) {
	increment(player, projectilesShot, 10);

	if (!projectileTime.containsKey(player.getName())) {
	    projectileTime.put(player.getName(), System.currentTimeMillis());
	    return true;
	} else if (projectilesShot.get(player.getName()) == 10) {
	    long time = System.currentTimeMillis() - projectileTime.get(player.getName());
	    projectileTime.remove(player.getName());
	    projectilesShot.remove(player.getName());
	    if (time < 1500)
		return true;
	}
	return false;
    }

    public boolean checkFastDrop(Player player) {
	increment(player, blocksDropped, 10);

	if (!blockTime.containsKey(player.getName())) {
	    blockTime.put(player.getName(), System.currentTimeMillis());
	    return true;
	} else if (blocksDropped.get(player.getName()) == 10) {
	    long time = System.currentTimeMillis() - blockTime.get(player.getName());
	    blockTime.remove(player.getName());
	    blocksDropped.remove(player.getName());

	    if (time < 800)
		return true;
	}

	return false;
    }

    public boolean checkLongReachBlock(Player player, double x, double y, double z) {
	if (isInstantBreakExempt(player))
	    return true;
	else {
	    player.getName();
	    double distance = player.getGameMode() == GameMode.CREATIVE ? 6.0
		    : player.getLocation().getDirection().getY() > 0.9 ? 6.0 : 5.5;
	    double i = x >= distance ? x : y > distance ? y : z > distance ? z : -1;

	    return i != -1;
	}
    }

    public boolean checkLongReachDamage(Player player, double x, double y, double z) {
	double i = x >= 5.9 ? x : y > 5.9 ? y : z > 5.9 ? z : -1;

	return i != -1;
    }

    public boolean checkSpider(Player player, double y) {
	return y <= 0.11761 && y >= 0.11759 && !CheatUtilities.isClimbableBlock(player.getLocation().getBlock());
    }

    public boolean checkYSpeed(Player player, double y) {
	return !isMovingExempt(player) && !player.isInsideVehicle() && !player.isSleeping() && y > 0.5
		&& !isDoing(player, velocitized, 3) && !player.hasPotionEffect(PotionEffectType.JUMP);
    }

    public boolean checkNoFall(Player player, double y) {
	String name = player.getName();

	if (player.getGameMode() != GameMode.CREATIVE && !player.isInsideVehicle() && !player.isSleeping()
		&& !isMovingExempt(player) && !justPlaced(player) && !CheatUtilities.isInWater(player)
		&& !CheatUtilities.isInWeb(player)) {
	    if (player.getFallDistance() == 0) {
		if (nofallViolation.get(name) == null) {
		    nofallViolation.put(name, 1);
		} else {
		    nofallViolation.put(name, nofallViolation.get(player.getName()) + 1);
		}

		int i = nofallViolation.get(name);

		if (i >= 9) {
		    nofallViolation.put(player.getName(), 1);
		    return true;
		} else
		    return false;
	    } else {
		nofallViolation.put(name, 0);
		return false;
	    }
	}
	return false;
    }

    public boolean checkXZSpeed(Player player, double x, double z) {
	if (!isSpeedExempt(player) && player.getVehicle() == null) {
	    double max = 0.25;

	    if (player.getLocation().getBlock().getType() == Material.SOUL_SAND) {
		if (player.isSprinting()) {
		    max = 0.2;
		} else if (player.hasPotionEffect(PotionEffectType.SPEED)) {
		    max = 0.16;
		} else {
		    max = 0.13;
		}
	    } else if (player.isFlying()) {
		max = 0.56;
	    } else if (player.hasPotionEffect(PotionEffectType.SPEED)) {
		if (player.isSprinting()) {
		    max = 0.95;
		} else {
		    max = 0.7;
		}
	    } else if (player.isSprinting()) {
		max = 0.65;
	    }

	    float speed = player.getWalkSpeed();
	    max += speed > 0 ? player.getWalkSpeed() - 0.2f : 0;

	    if (x > max || z > max) {
		int num = this.increment(player, speedViolation, 3);

		return num >= 3;
	    } else {
		speedViolation.put(player.getName(), 0);
		return false;
	    }
	} else
	    return false;
    }

    public boolean checkSneak(Player player, double x, double z) {
	if (player.isSneaking() && !player.isFlying() && !isMovingExempt(player) && !player.isInsideVehicle()) {
	    double i = x > 0.15 ? x : z > 0.15 ? z : -1;

	    return i != -1;
	} else
	    return false;
    }

    public boolean checkSprintHungry(PlayerToggleSprintEvent event) {
	Player player = event.getPlayer();

	return event.isSprinting() && player.getGameMode() != GameMode.CREATIVE && player.getFoodLevel() <= 6;
    }

    public boolean checkWaterWalk(Player player, double x, double y, double z) {
	Block block = player.getLocation().getBlock();

	if (player.getVehicle() == null && !player.isFlying()) {
	    if (block.isLiquid()) {
		if (isInWater.contains(player.getName())) {
		    if (isInWaterCache.contains(player.getName())) {
			if (player.getNearbyEntities(1, 1, 1).isEmpty()) {
			    boolean b;

			    if (!CheatUtilities.sprintFly(player)) {
				b = x > 0.19 || z > 0.19;
			    } else {
				b = x > 0.3 || z > 0.3;
			    }

			    if (!b && !CheatUtilities.isFullyInWater(player.getLocation())
				    && CheatUtilities.isHoveringOverWater(player.getLocation(), 1) && y == 0D
				    && !block.getType().equals(Material.WATER_LILY)) {
				b = true;
			    }

			    if (b) {
				if (waterSpeedViolation.containsKey(player.getName())) {
				    int v = waterSpeedViolation.get(player.getName());

				    if (v >= 4) {
					waterSpeedViolation.put(player.getName(), 0);
					return false;
				    } else {
					waterSpeedViolation.put(player.getName(), v + 1);
				    }
				} else {
				    waterSpeedViolation.put(player.getName(), 1);
				}
			    }
			}
		    } else {
			isInWaterCache.add(player.getName());
			return false;
		    }
		} else {
		    isInWater.add(player.getName());
		    return false;
		}
	    } else if (block.getRelative(BlockFace.DOWN).isLiquid() && isAscending(player)
		    && CheatUtilities.cantStandAt(block)
		    && CheatUtilities.cantStandAt(block.getRelative(BlockFace.DOWN))) {
		if (waterAscensionViolation.containsKey(player.getName())) {
		    int v = waterAscensionViolation.get(player.getName());

		    if (v >= 13) {
			waterAscensionViolation.put(player.getName(), 0);
			return false;
		    } else {
			waterAscensionViolation.put(player.getName(), v + 1);
		    }
		} else {
		    waterAscensionViolation.put(player.getName(), 1);
		}
	    } else {
		isInWater.remove(player.getName());
		isInWaterCache.remove(player.getName());
	    }
	}

	return false;
    }

    public boolean checkVClip(Player player, Distance distance) {
	double from = Math.round(distance.fromY());
	double to = Math.round(distance.toY());

	if (player.isInsideVehicle() || from == to || from < to || Math.round(distance.getYDifference()) < 2)
	    return false;

	for (int i = 0; i < Math.round(distance.getYDifference()) + 1; i++) {
	    Block block = new Location(player.getWorld(), player.getLocation().getX(), to + i,
		    player.getLocation().getZ()).getBlock();

	    if (block.getType() != Material.AIR && block.getType().isSolid())
		return true;
	}

	return false;
    }

    public boolean checkYAxis(Player player, Distance distance) {
	if (distance.getYDifference() > 400 || distance.getYDifference() < 0)
	    return false;

	if (!isMovingExempt(player) && !CheatUtilities.isClimbableBlock(player.getLocation().getBlock())
		&& !CheatUtilities.isClimbableBlock(player.getLocation().add(0, -1, 0).getBlock())
		&& !player.isInsideVehicle() && !CheatUtilities.isInWater(player)
		&& !player.hasPotionEffect(PotionEffectType.JUMP)) {
	    double y1 = player.getLocation().getY();
	    String name = player.getName();

	    if (!lastYcoord.containsKey(name) || !lastYtime.containsKey(name) || !yAxisLastViolation.containsKey(name)
		    || !yAxisLastViolation.containsKey(name)) {
		lastYcoord.put(name, y1);
		yAxisViolations.put(name, 0);
		yAxisLastViolation.put(name, 0L);
		lastYtime.put(name, System.currentTimeMillis());
	    } else {
		if (y1 > lastYcoord.get(name) && yAxisViolations.get(name) > 1
			&& System.currentTimeMillis() - yAxisLastViolation.get(name) < 5000) {
		    yAxisViolations.put(name, yAxisViolations.get(name) + 1);
		    yAxisLastViolation.put(name, System.currentTimeMillis());

		    return true;
		} else {
		    if (yAxisViolations.get(name) > 1
			    && System.currentTimeMillis() - yAxisLastViolation.get(name) > 5000) {
			yAxisViolations.put(name, 0);
			yAxisLastViolation.put(name, 0L);
		    }
		}

		long i = System.currentTimeMillis() - lastYtime.get(name);
		double diff = 5.0 + (CheatUtilities.isStair(player.getLocation().add(0, -1, 0).getBlock()) ? 0.5 : 0.0);

		if (y1 - lastYcoord.get(name) > diff && i < 1000) {
		    yAxisViolations.put(name, yAxisViolations.get(name) + 1);
		    yAxisLastViolation.put(name, System.currentTimeMillis());

		    return false;
		} else {
		    if (y1 - lastYcoord.get(name) > 5.0 + 1
			    || System.currentTimeMillis() - lastYtime.get(name) > 1000) {
			lastYtime.put(name, System.currentTimeMillis());
			lastYcoord.put(name, y1);
		    }
		}
	    }
	}

	return false;
    }

    public boolean checkTimer(Player player) {
	String name = player.getName();
	int step = 1;

	if (steps.containsKey(name)) {
	    step = steps.get(name) + 1;
	}

	if (step == 1) {
	    stepTime.put(name, System.currentTimeMillis());
	}

	increment(player, steps, step);

	if (step == 50) {
	    long time = System.currentTimeMillis() - stepTime.get(name);
	    steps.put(name, 0);
	    if (time < 2000)
		return true;
	}

	return false;
    }

    // public CheckResult checkSight(Player player, Entity entity) {
    // /*if (entity instanceof LivingEntity) {
    // LivingEntity le = (LivingEntity) entity;
    // // Check to make sure the entity's head is not surrounded
    // Block head = le.getWorld().getBlockAt((int) le.getLocation().getX(),
    // (int)
    // (le.getLocation().getY() + le.getEyeHeight()), (int)
    // le.getLocation().getZ());
    // boolean solid = false;
    // // TODO: This sucks. See if it's possible to not have as many
    // false-positives while still
    // retaining most of the check.
    // for (int x = -2; x <= 2; x++) {
    // for (int z = -2; z <= 2; z++) {
    // for (int y = -1; y < 2; y++) {
    // if (head.getRelative(x, y, z).getTypeId() != 0) {
    // if (head.getRelative(x, y, z).getType().isSolid()) {
    // solid = true;
    // break;
    // }
    //
    // }
    // }
    // }
    //
    // }
    // if (solid) {
    // return PASS;
    // }
    // // TODO: Needs proper testing
    // Location mobLocation = le.getEyeLocation();
    // for (Block block : player.getLineOfSight(transparent, 5)) {
    // if (Math.abs(block.getLocation().getX() - mobLocation.getX()) < 2.3 ||
    // Math.abs(block.getLocation().getZ() - mobLocation.getZ()) < 2.3) {
    // return PASS;
    // }
    // }
    // return new CheckResult(Result.FAILED, player.getName()+" tried to damage
    // an entity
    // ("+le.getType()+") out of sight ");
    // }*/
    // return PASS;
    // }

    public boolean checkFlight(Player player, Distance distance) {
	if (distance.getYDifference() > 400) // This was a teleport, so we don't
					     // care about it.
	    return false;

	final String name = player.getName();
	final double y1 = distance.fromY();
	final double y2 = distance.toY();

	if (!isMovingExempt(player) && !CheatUtilities.isHoveringOverWater(player.getLocation(), 1)
		&& CheatUtilities.cantStandAtExp(player.getLocation())
		&& CheatUtilities.blockIsnt(player.getLocation().getBlock().getRelative(BlockFace.DOWN),
			new Material[] { Material.FENCE, Material.FENCE_GATE, Material.COBBLE_WALL })) {
	    if (!blocksOverFlight.containsKey(name)) {
		blocksOverFlight.put(name, 0D);
	    }

	    blocksOverFlight.put(name, blocksOverFlight.get(name) + distance.getXDifference()
		    + distance.getYDifference() + distance.getZDifference());

	    if (y1 > y2) {
		blocksOverFlight.put(name, blocksOverFlight.get(name) - distance.getYDifference());
	    }

	    if (blocksOverFlight.get(name) > 15 && y1 <= y2)
		return true;
	} else {
	    blocksOverFlight.put(name, 0D);
	}

	return false;
    }

    public boolean checkAscension(Player player, double y1, double y2) {
	int max = 8;

	if (player.hasPotionEffect(PotionEffectType.JUMP)) {
	    max += 12;
	}

	Block block = player.getLocation().getBlock();

	if (!isMovingExempt(player) && !CheatUtilities.isInWater(player) && !justBroke(player)
		&& !CheatUtilities.isClimbableBlock(player.getLocation().getBlock()) && !player.isInsideVehicle()) {
	    String name = player.getName();

	    if (y1 < y2) {
		if (!block.getRelative(BlockFace.NORTH).isLiquid() && !block.getRelative(BlockFace.SOUTH).isLiquid()
			&& !block.getRelative(BlockFace.EAST).isLiquid()
			&& !block.getRelative(BlockFace.WEST).isLiquid()) {
		    increment(player, ascensionCount, max);

		    if (ascensionCount.get(name) >= max)
			return true;
		}
	    } else {
		ascensionCount.put(name, 0);
	    }
	}

	return false;
    }

    public boolean checkSwing(Player player, Block block) {
	String name = player.getName();

	if (!isInstantBreakExempt(player)) {
	    if (!player.getInventory().getItemInHand().containsEnchantment(Enchantment.DIG_SPEED)
		    && !(player.getInventory().getItemInHand().getType() == Material.SHEARS
			    && block.getType() == Material.LEAVES)) {
		if (blockPunches.get(name) != null && player.getGameMode() != GameMode.CREATIVE) {
		    int i = blockPunches.get(name);
		    if (i < 5)
			return true;
		    else {
			blockPunches.put(name, 0);
		    }
		}
	    }
	}

	return false;
    }

    public boolean checkFastBreak(Player player, Block block) {
	int violations = 2;
	long timemax = isInstantBreakExempt(player) ? 0
		: CheatUtilities.calcSurvivalFastBreak(player.getInventory().getItemInHand(), block.getType());

	if (player.getGameMode() == GameMode.CREATIVE) {
	    violations = 4;
	    timemax = 100;
	}

	String name = player.getName();

	if (!fastBreakViolation.containsKey(name)) {
	    fastBreakViolation.put(name, 0);
	} else {
	    Long math = System.currentTimeMillis() - lastBlockBroken.get(name);
	    int i = fastBreakViolation.get(name);

	    if (i > violations && math < 10000) {
		lastBlockBroken.put(name, System.currentTimeMillis());
		return true;
	    } else if (fastBreakViolation.get(name) > 0 && math > 10000) {
		fastBreakViolation.put(name, 0);
	    }
	}
	if (!fastBreaks.containsKey(name) || !lastBlockBroken.containsKey(name)) {
	    if (!lastBlockBroken.containsKey(name)) {
		lastBlockBroken.put(name, System.currentTimeMillis());
	    }
	    if (!fastBreaks.containsKey(name)) {
		fastBreaks.put(name, 0);
	    }
	} else {
	    Long math = System.currentTimeMillis() - lastBlockBroken.get(name);

	    if (math != 0L && timemax != 0L) {
		if (math < timemax) {
		    if (fastBreakViolation.containsKey(name) && fastBreakViolation.get(name) > 0) {
			fastBreakViolation.put(name, fastBreakViolation.get(name) + 1);
		    } else {
			fastBreaks.put(name, fastBreaks.get(name) + 1);
		    }
		    blockBreakHolder.put(name, false);
		}
		if (fastBreaks.get(name) >= 2 && math < timemax) {
		    fastBreaks.put(name, 0);
		    fastBreakViolation.put(name, fastBreakViolation.get(name) + 1);
		    return true;
		} else if (fastBreaks.get(name) >= 2 || fastBreakViolation.get(name) > 0) {
		    if (!blockBreakHolder.containsKey(name) || !blockBreakHolder.get(name)) {
			blockBreakHolder.put(name, true);
		    } else {
			fastBreaks.put(name, fastBreaks.get(name) - 1);

			if (fastBreakViolation.get(name) > 0) {
			    fastBreakViolation.put(name, fastBreakViolation.get(name) - 1);
			}

			blockBreakHolder.put(name, false);
		    }
		}
	    }
	}

	lastBlockBroken.put(name, System.currentTimeMillis());

	return false;
    }

    public boolean checkFastPlace(Player player) {
	int violations = player.getGameMode() == GameMode.CREATIVE ? 3 : 2;
	long time = System.currentTimeMillis();
	String name = player.getName();

	if (!lastBlockPlaceTime.containsKey(name) || !fastPlaceViolation.containsKey(name)) {
	    lastBlockPlaceTime.put(name, 0L);

	    if (!fastPlaceViolation.containsKey(name)) {
		fastPlaceViolation.put(name, 0);
	    }
	} else if (fastPlaceViolation.containsKey(name) && fastPlaceViolation.get(name) > violations) {
	    Long math = System.currentTimeMillis() - lastBlockPlaced.get(name);

	    if (lastBlockPlaced.get(name) > 0 && math < 10000) {
		lastBlockPlaced.put(name, time);
		return true;
	    } else if (lastBlockPlaced.get(name) > 0 && math > 10000) {
		fastPlaceViolation.put(name, 0);
	    }
	} else if (lastBlockPlaced.containsKey(name)) {
	    long last = lastBlockPlaced.get(name);
	    long lastTime = lastBlockPlaceTime.get(name);
	    long thisTime = time - last;

	    if (lastTime != 0 && thisTime < 50) {
		lastBlockPlaceTime.put(name, time - last);
		lastBlockPlaced.put(name, time);
		fastPlaceViolation.put(name, fastPlaceViolation.get(name) + 1);
		return true;
	    }

	    lastBlockPlaceTime.put(name, time - last);
	}

	lastBlockPlaced.put(name, time);

	return false;
    }

    public boolean checkInventoryClicks(Player player) {
	if (player.getGameMode() == GameMode.CREATIVE)
	    return false;

	String name = player.getName();
	int clicks = 1;

	if (inventoryClicks.containsKey(name)) {
	    clicks = inventoryClicks.get(name) + 1;
	}

	inventoryClicks.put(name, clicks);

	if (clicks == 1) {
	    inventoryTime.put(name, System.currentTimeMillis());
	} else if (clicks == 10) {
	    long time = System.currentTimeMillis() - inventoryTime.get(name);
	    inventoryClicks.put(name, 0);
	    if (time < 50)
		return true;
	}

	return false;
    }

    public boolean checkAutoTool(Player player) {
	return itemInHand.containsKey(player.getName())
		&& itemInHand.get(player.getName()) != player.getItemInHand().getType();
    }

    public boolean checkSprintDamage(Player player) {
	return isDoing(player, sprinted, 0.2);
    }

    public boolean checkAnimation(Player player, Entity e) {
	return !justAnimated(player);
    }

    public boolean checkFastHeal(Player player) {
	if (lastHeal.containsKey(player.getName())) {
	    long l = lastHeal.get(player.getName());
	    lastHeal.remove(player.getName());

	    if (System.currentTimeMillis() - l < 2000)
		return true;
	}
	return false;
    }

    public boolean checkFastEat(Player player) {
	if (startEat.containsKey(player.getName())) {
	    long l = startEat.get(player.getName());
	    startEat.remove(player.getName());

	    if (System.currentTimeMillis() - l < 1000)
		return true;
	}

	return false;
    }

    public boolean isInstantBreakExempt(Player player) {
	return isDoing(player, instantBreakExempt, 2);
    }

    public boolean isHoveringOverWaterAfterViolation(Player player) {
	if (waterSpeedViolation.containsKey(player.getName())) {
	    if (waterSpeedViolation.get(player.getName()) >= 4
		    && CheatUtilities.isHoveringOverWater(player.getLocation()))
		return true;
	}
	return false;
    }

    public boolean justBroke(Player player) {
	return isDoing(player, brokenBlock, 0.1);
    }

    public boolean justVelocity(Player player) {
	return velocitized.containsKey(player.getName())
		&& System.currentTimeMillis() - velocitized.get(player.getName()) < 2100;
    }

    public boolean extendVelocityTime(final Player player) {
	if (velocitytrack.containsKey(player.getName())) {
	    velocitytrack.put(player.getName(), velocitytrack.get(player.getName()) + 1);
	    if (velocitytrack.get(player.getName()) > 2) {
		velocitized.put(player.getName(), System.currentTimeMillis() + 5000);
		kPure.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(kPure.getInstance(),
			() -> velocitytrack.put(player.getName(), 0), 2 * 20L);
		return true;
	    }
	} else {
	    velocitytrack.put(player.getName(), 0);
	}

	return false;
    }

    public boolean justPlaced(Player player) {
	return isDoing(player, placedBlock, 0.1);
    }

    public void resetAnimation(final Player player) {
	animated.remove(player.getName());
	blockPunches.put(player.getName(), 0);
    }

    private boolean justAnimated(Player player) {
	String name = player.getName();
	if (animated.containsKey(name)) {
	    long time = System.currentTimeMillis() - animated.get(name);
	    int count = interactionCount.get(player.getName()) + 1;
	    interactionCount.put(player.getName(), count);

	    if (count > 3) {
		animated.remove(player.getName());
		return false;
	    }
	    return time < 200;
	} else
	    return false;
    }

    public boolean isMovingExempt(Player player) {
	return isDoing(player, movingExempt, -1);
    }

    public boolean isAscending(Player player) {
	return isAscending.contains(player.getName());
    }

    public boolean isSpeedExempt(Player player) {
	return isMovingExempt(player) || justVelocity(player);
    }

    private boolean isDoing(Player player, Map<String, Long> map, double max) {
	if (map.containsKey(player.getName())) {
	    if (max != -1) {
		if ((System.currentTimeMillis() - map.get(player.getName())) / 1000 > max) {
		    map.remove(player.getName());
		    return false;
		} else
		    return true;
	    } else {
		// Termination time has already been calculated
		if (map.get(player.getName()) < System.currentTimeMillis()) {
		    map.remove(player.getName());
		    return false;
		} else
		    return true;
	    }
	} else
	    return false;
    }

    public int increment(Player player, Map<String, Integer> map, int num) {
	String name = player.getName();
	if (map.get(name) == null) {
	    map.put(name, 1);
	    return 1;
	} else {
	    int amount = map.get(name) + 1;
	    if (amount < num + 1) {
		map.put(name, amount);
		return amount;
	    } else {
		map.put(name, num);
		return num;
	    }
	}
    }
}
