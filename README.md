# Telegram KHLScheduleBot

About bot
=====================
Unofficial telegram bot for [KHL](https://khl.ru).
Unofficial telegram channel: [@khl_unofficial](https://t.me/khl_unofficial)

1. This bot shows schedule of games for KHL clubs
 
 - Search it in the telegram: [@KHLScheduleBot](https://telegram.me/KHLScheduleBot)

2. Bot sends information to the unofficial telegram channel[@khl_unofficial](https://telegram.me/khl_unofficial): 

 - Information about current games in the KHL
 - Actual KHL news
 - Actual KHL videos
 - Standings by conference
 - Photo of the Day

Instruction for build and deploy
=====================
 1. Download or clone [repo](https://github.com/schepach/TelegramKHLBot.git)
 2. Set your botName and botToken in `KHLBot.java` class
 3. Build project with `maven` (`mvn clean package`)
 4. Go to the `target` folder and deploy `.ear` file. (For example, on Wildfly Application Server)
 5. Enjoy!