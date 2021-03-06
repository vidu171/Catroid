/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content;

import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.physics.PhysicsCollision;
import org.catrobat.catroid.physics.content.bricks.CollisionReceiverBrick;

public class CollisionScript extends BroadcastScript {

	private static final long serialVersionUID = 1L;

	public CollisionScript(String broadcastMessage) {
		super(broadcastMessage);
	}

	public CollisionObjectIdentifier splitBroadcastMessage() {
		String broadcastMessage = getBroadcastMessage();
		if (broadcastMessage == null) {
			return new CollisionObjectIdentifier("", "");
		}

		String[] collisionObjectIdentifierArray = broadcastMessage.split(PhysicsCollision.COLLISION_MESSAGE_CONNECTOR);
		if (collisionObjectIdentifierArray.length != 2) {
			return new CollisionObjectIdentifier("", "");
		}

		return new CollisionObjectIdentifier(collisionObjectIdentifierArray[0], collisionObjectIdentifierArray[1]);
	}

	@Override
	public ScriptBrick getScriptBrick() {
		if (brick == null) {
			brick = new CollisionReceiverBrick(this);
		}
		return brick;
	}

	@Override
	public Script copyScriptForSprite(Sprite copySprite) {
		CollisionScript cloneScript = new CollisionScript(receivedMessage);

		doCopy(copySprite, cloneScript);
		return cloneScript;
	}

	public void updateBroadcastMessage(String oldCollisionObjectIdentifier, String newCollisionObjectIdentifier) {
		CollisionObjectIdentifier collisionObjectIdentifier = splitBroadcastMessage();
		if (collisionObjectIdentifier.getCollisionObjectOneIdentifier().equals(oldCollisionObjectIdentifier)) {
			// update first object identifier
			String collisionObjectTwoIdentifier = collisionObjectIdentifier.getCollisionObjectTwoIdentifier();
			setAndReturnBroadcastMessage(newCollisionObjectIdentifier, collisionObjectTwoIdentifier);
		} else if (collisionObjectIdentifier.getCollisionObjectTwoIdentifier().equals(oldCollisionObjectIdentifier)) {
			// update second object identifier
			String collisionObjectOneIdentifier = collisionObjectIdentifier.getCollisionObjectOneIdentifier();
			setAndReturnBroadcastMessage(collisionObjectOneIdentifier, newCollisionObjectIdentifier);
		}
	}

	public String setAndReturnBroadcastMessage(String collisionObjectOneIdentifier, String collisionObjectTwoIdentifier) {
		String collisionBroadcastMessage = PhysicsCollision.generateBroadcastMessage(collisionObjectOneIdentifier,
				collisionObjectTwoIdentifier);
		setBroadcastMessage(collisionBroadcastMessage);
		return collisionBroadcastMessage;
	}

	public class CollisionObjectIdentifier {
		private String collisionObjectOneIdentifier;
		private String collisionObjectTwoIdentifier;

		public CollisionObjectIdentifier(String collisionObjectOneIdentifier, String collisionObjectTwoIdentifier) {
			this.collisionObjectOneIdentifier = collisionObjectOneIdentifier;
			this.collisionObjectTwoIdentifier = collisionObjectTwoIdentifier;
		}

		public String getCollisionObjectTwoIdentifier() {
			return collisionObjectTwoIdentifier;
		}

		public String getCollisionObjectOneIdentifier() {
			return collisionObjectOneIdentifier;
		}
	}
}
