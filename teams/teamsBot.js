const { TeamsActivityHandler, CardFactory, TurnContext, useBotState } = require("botbuilder");
const germanWelcomeCard = require("./adaptiveCards/german/welcome.json");
const englishWelcomeCard = require("./adaptiveCards/english/welcome.json");
const frenchWelcomeCard = require("./adaptiveCards/french/welcome.json");
const italianWelcomeCard = require("./adaptiveCards/italian/welcome.json");
const germanSystemMessageCard = require("./adaptiveCards/german/systemMessage.json");
const englishSystemMessageCard = require("./adaptiveCards/english/systemMessage.json");
const frenchSystemMessageCard = require("./adaptiveCards/french/systemMessage.json");
const italianSystemMessageCard = require("./adaptiveCards/italian/systemMessage.json");
const cardTools = require("@microsoft/adaptivecards-tools");
const ApiClient =  require("./apiclient");
const config = require('./config/config.json');
const englishConfig = require('./config/english.json');
const germanConfig = require('./config/german.json');
const frenchConfig = require('./config/french.json');
const italianConfig = require('./config/italian.json');

class TeamsBot extends TeamsActivityHandler {
  

 async hasOpenConversation(userId) {
    return new Promise((resolve, reject) => {
      var callback = function (error, data, response) {
        if (error) {
          reject(error);
        } else {
          resolve(data.hasOpenConversation);
        }
      };
      this.htBotApi.hasOpenConversation(userId, callback);
    });
  }

  async continueConversation(userId) {
    return new Promise((resolve, reject) => {
      var callback = function (error, data, response) {
        if (error) {
          reject(error);
        } else {
          resolve(data.resultCode);
        }
      };
      this.htBotApi.continueConversation(userId, callback);
    });
  }

  async getAnswer(prompt, userId, language) {
    return new Promise((resolve, reject) => {
      var callback = function (error, data, response) {
        if (error) {
          reject(error);
        } else {
          resolve(data.answer);
        }
      };
      this.htBotApi.getAnswer(prompt, userId, language, callback);
    });
  }

  async rateConversation(userId, positive) {
    return new Promise((resolve, reject) => {
      var callback = function (error, data, response) {
        if (error) {
          reject(error);
        } else {
          resolve(data.resultCode);
        }
      };
      this.htBotApi.rateConversation(userId, positive, callback);
    });
  }

  retrieveWelcomeCard(userLocale) {
    if(!userLocale) {
      return englishWelcomeCard;
    }

    var welcomeCard;

    if(userLocale.startsWith("en-")) {
      welcomeCard = englishWelcomeCard;
    } else if(userLocale.startsWith("de-")) {
      welcomeCard = germanWelcomeCard;
    } else if(userLocale.startsWith("it-")) {
      welcomeCard = italianWelcomeCard;
    } else if(userLocale.startsWith("fr-")) {
      welcomeCard = frenchWelcomeCard;
    }

    return welcomeCard;
  }

  retrieveSystemMessageCard(userLocale) {
    if(!userLocale) {
      return englishSystemMessageCard;
    }

    var systemMessageCard;

    if(userLocale.startsWith("en-")) {
      systemMessageCard = englishSystemMessageCard;
    } else if(userLocale.startsWith("de-")) {
      systemMessageCard = germanSystemMessageCard;
    } else if(userLocale.startsWith("it-")) {
      systemMessageCard = italianSystemMessageCard;
    } else if(userLocale.startsWith("fr-")) {
      systemMessageCard = frenchSystemMessageCard;
    }

    return systemMessageCard;
  }

  retrieveLanguageConfig(userLocale) {
    if(!userLocale) {
      return englishConfig;
    }

    var languageConfig;

    if(userLocale.startsWith("en-")) {
      languageConfig = englishConfig;
    } else if(userLocale.startsWith("de-")) {
      languageConfig = germanConfig;
    } else if(userLocale.startsWith("it-")) {
      languageConfig = italianConfig;
    } else if(userLocale.startsWith("fr-")) {
      languageConfig = frenchConfig;
    }

    return languageConfig;
  }

mapToLanguageEnum(userLocale) {
    if(!userLocale) {
      return "English";
    }

    var languageEnum;

    if(userLocale.startsWith("en-")) {
      languageEnum = "English";
    } else if(userLocale.startsWith("de-")) {
      languageEnum = "German";
    } else if(userLocale.startsWith("it-")) {
      languageEnum = "Italian";
    } else if(userLocale.startsWith("fr-")) {
      languageEnum = "French"
    }

    return languageEnum;
  }

  constructor() {
    super();

    var client = new ApiClient.ApiClient(config.apiUrl);
    client.defaultHeaders = {
        'X-API-Key': config.apiKey
    }
    this.htBotApi = new ApiClient.DefaultApi(client);

    this.onMembersAdded(async (context, next) => {
      const membersAdded = context.activity.membersAdded;
      for (let cnt = 0; cnt < 1; cnt++) {
        if (membersAdded[cnt].id) {
        var welcomeCard = this.retrieveWelcomeCard(membersAdded.locale);

        const card = cardTools.AdaptiveCards.declareWithoutData(welcomeCard).render();
        await context.sendActivity({ attachments: [CardFactory.adaptiveCard(card)] });
        break;
        }
      }
      await next();
    });


    this.onMessage(async (context, next) => {
      var userId = context.activity.from.id;
      var language = context.activity.locale;
      var prompt = context.activity.text.trim();

      switch (prompt) {
        case "welcome": {

          var welcomeCard = this.retrieveWelcomeCard(language);
          const card = cardTools.AdaptiveCards.declareWithoutData(welcomeCard).render();
          await context.sendActivity({ attachments: [CardFactory.adaptiveCard(card)] });
          return;
        }
      }


      try {
        if (await this.hasOpenConversation(userId)) {
          context.sendActivity(this.retrieveLanguageConfig(language).openConversation);
        } else {
          var botAnswer = await this.getAnswer(prompt, userId, this.mapToLanguageEnum(language));
          var answerobj = {answer: botAnswer};
          var systemMessageCard = this.retrieveSystemMessageCard(language);
          const card = cardTools.AdaptiveCards.declare(systemMessageCard).render(answerobj);
          await context.sendActivity({ attachments: [CardFactory.adaptiveCard(card)] });
          return;
        }
      } catch (error) {
        console.error(error);
      }
      await next();
    });
  }

  // Invoked when an action is taken on an Adaptive Card. The Adaptive Card sends an event to the Bot and this
  // method handles that event.
  //TODO: Make this work
  async onAdaptiveCardInvoke(context, invokeValue) {
    if (invokeValue.action.verb === "continueConversation") {
    var reply = await this.continueConversation(context.activity.from.id);
    var systemMessageCard = this.retrieveSystemMessageCard(context.activity.locale);
    
    //TODO: Translate
    //TODO: Retrieve actual answer
    var adaptiveCardObject = {answer: "blabla", rating: "You have continued the conversation"};

      const card = cardTools.AdaptiveCards.declare(systemMessageCard).render(adaptiveCardObject);
      await context.updateActivity({
        type: "message",
        id: context.activity.replyToId,
        attachments: [CardFactory.adaptiveCard(card)],
      });
      return { statusCode: 200 };
    } else if (invokeValue.action.verb === "negativeRating") {
      this.rateConversation(context.activity.from.id, false);
      var systemMessageCard = this.retrieveSystemMessageCard(context.activity.locale);
      
      //TODO: Translate
      //TODO: Retrieve actual answer
      var adaptiveCardObject = {rating: "You rated the conversation as negative"};
  
        const card = cardTools.AdaptiveCards.declare(systemMessageCard).render(adaptiveCardObject);
        await context.updateActivity({
          type: "message",
          id: context.activity.replyToId,
          attachments: [CardFactory.adaptiveCard(card)],
        });
        return { statusCode: 200 };
    } else if (invokeValue.action.verb === "positiveRating") {
      this.rateConversation(context.activity.from.id, false);
      var systemMessageCard = this.retrieveSystemMessageCard(context.activity.locale);
      
      //TODO: Translate
      //TODO: Retrieve actual answer
      var adaptiveCardObject = {answer: "blabla", rating: "You rated the conversation as positive"};
  
        const card = cardTools.AdaptiveCards.declare(systemMessageCard).render(adaptiveCardObject);
        await context.updateActivity({
          type: "message",
          id: context.activity.replyToId,
          attachments: [CardFactory.adaptiveCard(card)],
        });
        return { statusCode: 200 };
    }
  }
}

module.exports.TeamsBot = TeamsBot;
