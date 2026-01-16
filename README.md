# MCRecipes

This repository contains crafting recipe images (under the [recipe_images](/recipe_images) folder) for all Minecraft java edition recipes that were provided by https://minecraft-api.vercel.app/api/crafting-recipes . Others may have been added however aren't listed. If you cannot find a recipe and it is a valid Minecraft one, please make an issue.   
A MASSIVE THANK YOU to https://minecraft-api.vercel.app because the images and recipes all come from there! Thank you so much!
However the main project is a slackbot which gives these images on demand!

## Usage
To use the slackbot, just do @MCRecipes and type the item you want next to it.  
For example, `@MCRecipes oak button`  
It will then reply in a thread with your recipe!  
In case you make a typo or mistake, do not worry!  
It will use Gemini 2.5 Flash Lite to fix the mistake and provide the recipe to you, however it may take slightly longer.  
This is powered by ai.hackclub.com, thank you!  

## Self-hosting
1. Clone repo
2. Set environment variables in .env file
- You require the following:
- SLACK_APP_TOKEN (the one that begins with xapp)
- SLACK_BOT_TOKEN (the one that begins with xoxb)
- API_KEY (from ai.hackclub.com)
3. Run './gradlew run'

