# domeguard-1.5

Purpur/Paper 1.21.4 plugin: configurable elliptical dome (X/Z), Y limits, gradual Warden-style effects, damage immunity selector, sleep curse after crossing the lethal boundary, and Suspicious Stew cure.

## Commands
`/dome` — admin GUI (`dome.admin`).

## Boundary
0–10 blocks: nausea, slowness, weakness, mining fatigue gradually increase.
10–50 blocks: darkness/blindness and Warden sounds progressively intensify.
51+ blocks: cursed for sleep; non-immune players die.
Returning to 41 blocks outside-or-less clears potion effects; implementation clears all active potion effects when the player is back inside the dome within the configured clear distance.

## Sleep curse
Crossing the lethal distance marks the player permanently (UUID persisted in `plugins/DomeGuard/players.yml`). Beds are blocked until the player consumes Suspicious Stew.

## Damage immunity
`/dome` → `Иммунитет к урону` → click a player. The selected player receives all boundary effects and the sleep curse, but does not receive boundary damage or lethal death from the dome.
