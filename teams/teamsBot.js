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
const defaultSystemMessageProperties = require('./adaptiveCards/defaultSystemMessageProperties.json');

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

  copyJSONObject(jsonObject) {
    return JSON.parse(JSON.stringify(jsonObject));
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
          var answerobj = this.copyJSONObject(defaultSystemMessageProperties);
          answerobj.answer = botAnswer;
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
  async onAdaptiveCardInvoke(context, invokeValue) {

    var locale = context.activity.locale;
    var userId = context.activity.from.id;
    var systemMessageCard = this.retrieveSystemMessageCard(locale);
    var answerObj = this.copyJSONObject(defaultSystemMessageProperties);
    var message = null;

    if (invokeValue.action.verb === "continueConversation") {
    var reply = await this.continueConversation(userId);

    answerObj.answer = invokeValue.action.id;
    answerObj.continueConversationTextVisible = true;
    answerObj.buttonVisible = false;

    message = this.retrieveLanguageConfig(context.activity.locale).continueConversation;

    } else if (invokeValue.action.verb === "negativeRating") {
      var reply = await this.rateConversation(context.activity.from.id, false);
      systemMessageCard = this.retrieveSystemMessageCard(context.activity.locale);

      answerObj.answer = invokeValue.action.id;
      answerObj.incidentReportVisible = true;
      answerObj.incidentReport = "to be implemented";
      answerObj.negativeRatingVisible = true;
      answerObj.buttonVisible = false;

      message = this.retrieveLanguageConfig(context.activity.locale).negativeRating;
    } else if (invokeValue.action.verb === "positiveRating") {
      var reply = await this.rateConversation(context.activity.from.id, false);

      answerObj.answer = invokeValue.action.id;
      answerObj.positiveRatingVisible = true;
      answerObj.buttonVisible = false;

      message = this.retrieveLanguageConfig(context.activity.locale).positiveRating;
    }

    const card = cardTools.AdaptiveCards.declare(systemMessageCard).render(answerObj);
    await context.updateActivity({
      type: "message",
      id: context.activity.replyToId,
      attachments: [CardFactory.adaptiveCard(card)],
    });
    context.sendActivity(message);
    return { statusCode: 200 };
  }
}

module.exports.TeamsBot = TeamsBot;
