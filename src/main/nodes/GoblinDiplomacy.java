package main.nodes;

import org.osbot.rs07.api.Bank;
import org.osbot.rs07.api.DepositBox;
import org.osbot.rs07.api.GrandExchange;
import org.osbot.rs07.api.Quests.Quest;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.utility.ConditionalSleep;

import main.obj.Ores;
import main.obj.Pickaxes;
import main.obj.Resources;
import main.util.Miner;
import main.walking.Areas;

public class GoblinDiplomacy extends Node {
	
	// Write out all the steps
	
	// 1) Get 30-40 clay and bank it
	// 2) Sell the clay in the GE
	// 3) Buy 3 x goblin mail, orange dye, blue dye
	// 4) Use the dyes on two of the goblin mail
	// 5) turn in the quest
	
	private int amountOfClayToSell;
	private int amountOfClayToMine;
	private boolean buying;
	private int initialAmountOfClay;
	private boolean useResources;
	private boolean turnInQuest;
	private boolean selling;
	private boolean mining;
	
	private Miner miner;
	
	public GoblinDiplomacy(Script s, Miner miner) {
		super(s);
		this.miner = miner;
		amountOfClayToSell = 0; // Script.random(90, 100);
		amountOfClayToMine = amountOfClayToSell;
		buying = false;
		initialAmountOfClay = 0;
		useResources = false;
		turnInQuest = false;
		selling = false;
		mining = true;
	}

	@Override
	public boolean validate() throws InterruptedException {
		if(s.getQuests().isComplete(Quest.DORICS_QUEST) && !s.getQuests().isComplete(Quest.GOBLIN_DIPLOMACY) && s.getInventory().contains(Pickaxes.BRONZE.toString()) && s.getInventory().getAmount(Pickaxes.BRONZE.toString()) < 2
				&& s.getInventory().onlyContains(Pickaxes.BRONZE.toString(), Resources.COINS.toString(), Resources.BLUE_DYE.toString(), Ores.CLAY.toString(),
						Resources.ORANGE_DYE.toString(), Resources.GOBLIN_MAIL.toString(), Resources.BLUE_GOBLIN_MAIL.toString(), Resources.ORANGE_GOBLIN_MAIL.toString()))
			return true;
		return false;
	}

	@Override
	public boolean execute() throws InterruptedException {
		if(mining)
			mineClay();
		else if(selling) // amountOfClayToMine <= 0 && !buying && !hasRequiredTurnInItems() && !useResources && !turnInQuest
			sellClay();
		else if(buying)
			buyResources();
		else if(useResources)
			useResources();
		else
			turnInQuest();
		return false;
	}

	private void mineClay() {
		if(amountOfClayToMine <= 0) {
			mining = false;
			selling = true;
		} else {
			if(s.getInventory().isFull())
				depositBoxClay();
			else if(!Areas.RIMMINGTON_MINE_CLAY.contains(s.myPlayer()))
				s.getWalking().webWalk(Areas.RIMMINGTON_MINE_CLAY.getArea());
			else
				miner.mineClosest(Ores.CLAY);
			
			if(s.getInventory().getAmount(Ores.CLAY.toString()) > initialAmountOfClay) {
				amountOfClayToMine--;
				initialAmountOfClay = (int) s.getInventory().getAmount(Ores.CLAY.toString());
			}
		}
	}
	
	private void depositBoxClay() {
		if(!Areas.PORT_SARIM_DEPOSIT.contains(s.myPlayer()))
			s.getWalking().webWalk(Areas.PORT_SARIM_DEPOSIT.getArea());
		else {
			DepositBox db = s.getDepositBox();
			if(db != null) {
				if(!db.isOpen()) {
					if(db.open()) {
						new ConditionalSleep(5000) { 
							@Override
							public boolean condition() throws InterruptedException {
								return db.isOpen();
							}
						}.sleep();
					}
				} else {
					if(db.depositAll(Ores.CLAY.toString())) {
						new ConditionalSleep(5000) { 
							@Override
							public boolean condition() throws InterruptedException {
								return s.getInventory().isEmpty();
							}
						}.sleep();
						initialAmountOfClay = (int) s.getInventory().getAmount(Ores.CLAY.toString());
					}
				}
			}
		}
	}

	private void sellClay() {
		if(!Banks.GRAND_EXCHANGE.contains(s.myPlayer()))
			s.getWalking().webWalk(Banks.GRAND_EXCHANGE);
		else if(!s.getInventory().contains(Ores.CLAY.toString()) || !s.getInventory().getItem(Ores.CLAY.toString()).isNote()) {
			
			Bank bank = s.getBank();
			if(!bank.isOpen()) {
				try {
					if(bank.open()) {
						new ConditionalSleep(5000) { 
							@Override
							public boolean condition() throws InterruptedException {
								return bank.isOpen();
							}
						}.sleep();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else if(s.getInventory().contains(Ores.CLAY.toString())) {
				if(bank.depositAll(Ores.CLAY.toString())) {
					new ConditionalSleep(5000) {
						@Override
						public boolean condition() throws InterruptedException {
							return !s.getInventory().contains(Ores.CLAY.toString());
						}
					}.sleep();
				}
			} else if(!s.getInventory().contains(Ores.CLAY.toString())) {
				if(bank.enableMode(Bank.BankMode.WITHDRAW_NOTE)) {
					if(bank.withdrawAll(Ores.CLAY.toString())) {
						new ConditionalSleep(5000) {
							@Override
							public boolean condition() throws InterruptedException {
								return s.getInventory().contains(Ores.CLAY.toString()) && s.getInventory().getItem(Ores.CLAY.toString()).isNote();
							}
						}.sleep();
					}
				}
			}
			
		} else {
			if(!s.getGrandExchange().isOpen()) {
				NPC geClerk = s.getNpcs().closest("Grand Exchange Clerk");
				if(geClerk != null) {
					if(geClerk.interact("Exchange")) {
						new ConditionalSleep(5000) {
							@Override
							public boolean condition() throws InterruptedException {
								return s.getGrandExchange().isOpen();
							}
						}.sleep();
					}
				}
			} else {
				if(!s.getGrandExchange().isOfferScreenOpen()) {
					if(s.getGrandExchange().sellItems(GrandExchange.Box.BOX_1)) {
						new ConditionalSleep(4000) {
							@Override
							public boolean condition() throws InterruptedException {
								return s.getGrandExchange().isOfferScreenOpen();
							}
						}.sleep();
					}
				} else {
					if(s.getInventory().interact("Offer", Ores.CLAY.toString())) {
						new ConditionalSleep(4000) {
							@Override
							public boolean condition() throws InterruptedException {
								return s.getGrandExchange().isOfferScreenOpen();
							}
						}.sleep();
						if(s.getGrandExchange().setOfferPrice(100)) {
							if(s.getGrandExchange().setOfferQuantity( (int) s.getInventory().getAmount(Ores.CLAY.toString()))) {
								if(s.getGrandExchange().confirm()) {
									new ConditionalSleep(4000) {
										@Override
										public boolean condition() throws InterruptedException {
											return s.getGrandExchange().isOpen() && !s.getGrandExchange().isOfferScreenOpen();
										}
									}.sleep();
									try {
										Script.sleep(10000);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									if(s.getGrandExchange().getStatus(GrandExchange.Box.BOX_1) == GrandExchange.Status.FINISHED_SALE) {
										if(s.getGrandExchange().collect()) {
											new ConditionalSleep(4000) {
												@Override
												public boolean condition() throws InterruptedException {
													return s.getGrandExchange().getStatus(GrandExchange.Box.BOX_1) == GrandExchange.Status.EMPTY;
												}
											}.sleep();
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		if(s.getInventory().contains(Resources.COINS.toString())) {
			selling = false;
			buying = true;
		}
	}
	
	private void buyResources() {
		
		if(!Banks.GRAND_EXCHANGE.contains(s.myPlayer()))
			s.getWalking().webWalk(Banks.GRAND_EXCHANGE);
		else {
			if(!s.getInventory().contains(Resources.GOBLIN_MAIL.toString())) {
				buyItem(Resources.GOBLIN_MAIL, 1000, 3);
			} else if(!s.getInventory().contains(Resources.BLUE_DYE.toString()) && !s.getInventory().contains(Resources.BLUE_GOBLIN_MAIL.toString())) {
				buyItem(Resources.BLUE_DYE, 3000, 1);
			} else if(!s.getInventory().contains(Resources.ORANGE_DYE.toString()) && !s.getInventory().contains(Resources.ORANGE_GOBLIN_MAIL.toString())) {
				buyItem(Resources.ORANGE_DYE, 3000, 1);
			} else {
				buying = false;
				useResources = true;
			}
		}
		
		/*
		if(s.getInventory().contains(Resources.GOBLIN_MAIL.toString(), Resources.ORANGE_DYE.toString(), Resources.BLUE_DYE.toString()))
			buying = false;
		
		s.getGrandExchange().buyItem(Resources.GOBLIN_MAIL.getID(), Resources.GOBLIN_MAIL.toString(), 800, 3);
		s.getGrandExchange().buyItem(Resources.BLUE_DYE.getID(), Resources.BLUE_DYE.toString(), 500, 1);
		s.getGrandExchange().buyItem(Resources.ORANGE_DYE.getID(), Resources.ORANGE_DYE.toString(), 500, 1);
		
		if(!Banks.GRAND_EXCHANGE.contains(s.myPlayer()))
			s.getWalking().webWalk(Banks.GRAND_EXCHANGE);
		else {
			if(!s.getGrandExchange().isOpen()) {
				RS2Object geBooth = s.getObjects().closest("Grand Exchange booth");
				if(geBooth != null) {
					if(geBooth.interact("Exchange")) {
						new ConditionalSleep(10000) {
							@Override
							public boolean condition() throws InterruptedException {
								return s.getGrandExchange().isOpen();
							}
						}.sleep();
					}
				}
			} else {
				if(s.getGrandExchange().sellItems(Box.BOX_1)) {
					new ConditionalSleep(10000) {
						@Override
						public boolean condition() throws InterruptedException {
							return s.getGrandExchange().isSellOfferOpen();
						}
					}.sleep();
					if(s.getInventory().interact("Offer", Pickaxes.BRONZE.toString())) {
						new ConditionalSleep(10000) {
							@Override
							public boolean condition() throws InterruptedException {
								return s.getGrandExchange().isSellOfferOpen();
							}
						}.sleep();
						if(s.getGrandExchange().setOfferPrice(2)) {
							new ConditionalSleep(10000) {
								@Override
								public boolean condition() throws InterruptedException {
									return s.getGrandExchange().isOpen();
								}
							}.sleep();
							if(s.getGrandExchange().confirm()) {
								new ConditionalSleep(10000) {
									@Override
									public boolean condition() throws InterruptedException {
										return s.getGrandExchange().isOpen();
									}
								}.sleep();
								try {
									Script.sleep(5000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								if(s.getGrandExchange().collect()) {
									new ConditionalSleep(10000) {
										@Override
										public boolean condition() throws InterruptedException {
											return s.getGrandExchange().getStatus(Box.BOX_1) == GrandExchange.Status.EMPTY;
										}
									}.sleep();
								}
							}
						}
					}
				}
			}
		}
		*/
	}
	
	private void buyItem(Resources resource, int price, int amount) {
		if(!s.getGrandExchange().isOpen()) {
			NPC geClerk = s.getNpcs().closest("Grand Exchange Clerk");
			if(geClerk != null) {
				if(geClerk.interact("Exchange")) {
					new ConditionalSleep(5000) {
						@Override
						public boolean condition() throws InterruptedException {
							return s.getGrandExchange().isOpen();
						}
					}.sleep();
				}
			}
		} else {
			if(!s.getGrandExchange().isBuyOfferOpen()) {
				if(s.getGrandExchange().buyItems(GrandExchange.Box.BOX_1)) {
					new ConditionalSleep(5000) {
						@Override
						public boolean condition() throws InterruptedException {
							return s.getGrandExchange().isBuyOfferOpen();
						}
					}.sleep();
				}
			} else {
				if(s.getGrandExchange().buyItem(resource.getID(), resource.toString(), price, amount)) {
					s.log("about to collect");
					try {
						Script.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if(s.getGrandExchange().collect()) {
						new ConditionalSleep(5000) {
							@Override
							public boolean condition() throws InterruptedException {
								return s.getInventory().contains(resource.toString()) && s.getInventory().getAmount(resource.toString()) >= amount;
							}
						}.sleep();
					}
				}
			}
			
		}
	}
	
	private void useResources() {
		s.log("entering use resources");
		if(!s.getInventory().contains(Resources.GOBLIN_MAIL.toString()) || s.getInventory().getItem(Resources.GOBLIN_MAIL.toString()).isNote()) {
			Bank bank = s.getBank();
			if(bank != null) {
				if(!bank.isOpen()) {
					try {
						if(bank.open()) {
							new ConditionalSleep(5000) {
								@Override
								public boolean condition() throws InterruptedException {
									return bank.isOpen();
								}
							}.sleep();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					if(s.getInventory().contains(Resources.GOBLIN_MAIL.toString())) {
						if(bank.depositAll(Resources.GOBLIN_MAIL.toString())) {
							new ConditionalSleep(5000) {
								@Override
								public boolean condition() throws InterruptedException {
									return !s.getInventory().contains(Resources.GOBLIN_MAIL.toString());
								}
							}.sleep();
						}
					} else {
						if(bank.withdrawAll(Resources.GOBLIN_MAIL.toString())) {
							new ConditionalSleep(5000) {
								@Override
								public boolean condition() throws InterruptedException {
									return s.getInventory().contains(Resources.GOBLIN_MAIL.toString());
								}
							}.sleep();
						}
					}
					
				}
			}
		} else {
			if(s.getBank().isOpen()) {
				if(s.getBank().close()) {
					new ConditionalSleep(5000) {
						@Override
						public boolean condition() throws InterruptedException {
							return !s.getBank().isOpen();
						}
					}.sleep();
				}
			} else {
				if(!s.getInventory().contains(Resources.BLUE_GOBLIN_MAIL.toString())) {
					if(s.getInventory().getItem(Resources.BLUE_DYE.toString()).interact("Use")) {
						if(s.getInventory().getItem(Resources.GOBLIN_MAIL.toString()).interact("Use")) {
							new ConditionalSleep(5000) {
								@Override
								public boolean condition() throws InterruptedException {
									return s.getInventory().contains(Resources.BLUE_GOBLIN_MAIL.toString());
								}
							}.sleep();
						}
					}
				} else if(!s.getInventory().contains(Resources.ORANGE_GOBLIN_MAIL.toString())) {
					if(s.getInventory().getItem(Resources.ORANGE_DYE.toString()).interact("Use")) {
						if(s.getInventory().getItem(Resources.GOBLIN_MAIL.toString()).interact("Use")) {
							new ConditionalSleep(5000) {
								@Override
								public boolean condition() throws InterruptedException {
									return s.getInventory().contains(Resources.ORANGE_GOBLIN_MAIL.toString());
								}
							}.sleep();
						}
					}
				}
			}
		}
		
		if(s.getInventory().contains(Resources.GOBLIN_MAIL.toString()) && s.getInventory().contains(Resources.BLUE_GOBLIN_MAIL.toString()) && s.getInventory().contains(Resources.ORANGE_GOBLIN_MAIL.toString())) {
			useResources = false;
			turnInQuest = true;
		}
		
	}
	
	private boolean hasRequiredTurnInItems() {
		if(s.getInventory().contains(Resources.GOBLIN_MAIL.toString(), Resources.BLUE_DYE.toString(), Resources.ORANGE_DYE.toString()) && s.getInventory().getAmount(Resources.GOBLIN_MAIL.toString()) >= 3 )
			return true;
		return false;
	}
	
	
	// 62 is for quest completion progress. 1021 appears to be for location with 192 referring to in the cut scene
	private void turnInQuest() {
		s.log("turn in quest");
		
		if(s.getConfigs().get(1021) == 192) {
			if(s.getDialogues().inDialogue()) {
				s.getDialogues().clickContinue();
			}
		} else {
			if(!Areas.GOBLIN_VILLAGE_HALL.contains(s.myPlayer()))
				s.getWalking().webWalk(Areas.GOBLIN_VILLAGE_HALL.getArea());
			else {
				NPC generalWartFace = s.getNpcs().closest("General Wartface");
				if(generalWartFace != null) {
					if(!s.getDialogues().inDialogue()) {
						if(generalWartFace.interact("Talk-to")) {
							new ConditionalSleep(5000) {
								@Override
								public boolean condition() throws InterruptedException {
									return s.getDialogues().inDialogue();
								}
							}.sleep();
						}
					} else {
						if(s.getDialogues().isPendingContinuation()) {
							s.getDialogues().clickContinue();
						} else if(s.getDialogues().isPendingOption()) {
							try {
								s.getDialogues().completeDialogue("Do you want me to pick an armour colour for you?", "What about a different colour?", 
										"I have some orange armour here", "I have some blue armour here", "I have some brown armour here");
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		
	}
	
	public int getAmountOfClayToMine() {
		return amountOfClayToMine;
	}

}
