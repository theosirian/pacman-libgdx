package com.theosirian.pacman;

import com.theosirian.pacman.entity.Entity;
import com.theosirian.pacman.entity.Pacman;
import com.theosirian.pacman.pacdot.BonusPacdot;
import com.theosirian.pacman.pacdot.Pacdot;

import java.util.*;

import static com.theosirian.pacman.entity.Entity.Direction.*;

/**
 * Created by theosirian on 22/09/2016.
 */
public class Ai {
	public static Pacdot decidePacdot(Pacman pacman, List<Pacdot> pacdots) {
		Optional<Pacdot> nearestOpt = pacdots.stream().min((a, b) -> {
			if (a.isDestroy()) return Integer.MIN_VALUE;
			else if (b.isDestroy()) return Integer.MAX_VALUE;
			else if (a instanceof BonusPacdot) return Integer.MAX_VALUE;
			else if (b instanceof BonusPacdot) return Integer.MIN_VALUE;
			return heuristic_sort(pacman, a.getX(), a.getY(), b.getX(), b.getY());
		});
		if (nearestOpt.isPresent())
			return nearestOpt.get();
		return null;
	}

	public static void pathfind(int pacmanGraph[][], Pacman pacman, Queue<Entity.Direction> stepQueue, List<Integer> teleportHashes, Pacdot destination) {
		Queue<Prioritized<Integer>> frontier = new PriorityQueue<>((a, b) -> a.priority - b.priority);
		Map<Integer, Integer> cameFrom = new HashMap<>();
		Map<Integer, Integer> costSoFar = new HashMap<>();

		Integer px = (int) (pacman.getX() / 16f);
		Integer py = (int) (pacman.getY() / 16f);
		Integer ph = make_hash(pacman, px, py);

		frontier.add(new Prioritized<>(ph, 0));
		cameFrom.put(ph, null);
		costSoFar.put(ph, 0);

		if (destination != null) {
			destination.setTargeted();

			Integer gx = (int) (destination.getX() / 16f);
			Integer gy = (int) (destination.getY() / 16f);
			Integer gh = make_hash(pacman, gx, gy);

			Prioritized<Integer> curr;
			while (!frontier.isEmpty()) {
				curr = frontier.poll();

				if (Objects.equals(curr.val, gh)) break;

				for (int next = 0; next < pacmanGraph.length; next++) {
					if (pacmanGraph[curr.val][next] == 1) {
						int new_cost = costSoFar.get(curr.val) + 1;
						if (!costSoFar.containsKey(next) || new_cost < costSoFar.get(next)) {
							costSoFar.put(next, new_cost);
							int priority = new_cost + heuristic(pacman, ph, gh);
							frontier.add(new Prioritized<>(next, priority));
							cameFrom.put(next, curr.val);
						}
					}
				}
			}

			List<Entity.Direction> subSteps = new ArrayList<>();
			Integer child = gh;
			Integer parent = cameFrom.get(child);
			boolean teleportFlag = false;
			while (parent != null) {
				Entity.Direction dir = make_dir(pacman, parent, child);
				if (!teleportFlag) {
					for (int i = 0; i < 8; i++) subSteps.add(dir);
					if (teleportHashes.contains(parent)) {
						for (int i = 0; i < 8; i++) subSteps.add(dir);
						teleportFlag = true;
					}
				} else {
					teleportFlag = false;
				}
				child = parent;
				parent = cameFrom.get(parent);
			}
			Collections.reverse(subSteps);
			stepQueue.addAll(subSteps);
		}
	}

	private static Entity.Direction make_dir(Pacman pacman, int from, int to) {
		int fromy = from % pacman.getCollisionLayer().getHeight();
		int fromx = (from - fromy) / pacman.getCollisionLayer().getHeight();
		int toy = to % pacman.getCollisionLayer().getHeight();
		int tox = (to - toy) / pacman.getCollisionLayer().getHeight();

		if (fromx == tox) {
			if (fromy > toy) {
				return DOWN;
			} else {
				return UP;
			}
		}
		if (fromy == toy) {
			if (fromx > tox) {
				return LEFT;
			} else {
				return RIGHT;
			}
		}
		return NONE;
	}

	private static int make_hash(Pacman pacman, int i, int j) {
		return i * pacman.getCollisionLayer().getHeight() + j;
	}

	private static int heuristic_sort(Pacman pacman, int ax, int ay, int bx, int by) {
		int px = pacman.getX();
		int py = pacman.getY();
		int ad, bd;
		switch (pacman.getHeuristic()) {
			case EUCLIDEAN:
				ad = (int) Math.sqrt(Math.pow(ax - px, 2) + Math.pow(ay - py, 2));
				bd = (int) Math.sqrt(Math.pow(bx - px, 2) + Math.pow(by - py, 2));
				return ad - bd;
			case MANHATTAN:
				ad = Math.abs(ax - px) + Math.abs(ay - py);
				bd = Math.abs(bx - px) + Math.abs(by - py);
				return ad - bd;
		}
		return 0;
	}

	private static int heuristic(Pacman pacman, Integer ph, Integer gh) {
		int py = ph % pacman.getCollisionLayer().getHeight();
		int px = (ph - py) / pacman.getCollisionLayer().getHeight();
		int gy = gh % pacman.getCollisionLayer().getHeight();
		int gx = (gh - gy) / pacman.getCollisionLayer().getHeight();
		switch (pacman.getHeuristic()) {
			case EUCLIDEAN:
				return (int) Math.sqrt(Math.pow(gx - px, 2) + Math.pow(gy - py, 2));
			case MANHATTAN:
				return Math.abs(gx - px) + Math.abs(gy - py);
		}
		return 0;
	}

	private static class Prioritized<T> {
		T val;
		Integer priority;

		public Prioritized(T val, Integer priority) {
			this.val = val;
			this.priority = priority;
		}
	}
}
