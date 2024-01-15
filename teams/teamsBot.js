const { TeamsActivityHandler, CardFactory, TurnContext, useBotState } = require("botbuilder");
const germanWelcomeCard = require("./adaptiveCards/german/welcome.json");
const englishWelcomeCard = require("./adaptiveCards/english/welcome.json");
const frenchWelcomeCard = require("./adaptiveCards/french/welcome.json");
const italianWelcomeCard = require("./adaptiveCards/italian/welcome.json");
const germanSystemMessageCard = require("./adaptiveCards/german/systemMessage.json");
const englishSystemMessageCard = require("./adaptiveCards/english/systemMessage.json");
const frenchSystemMessageCard = require("./adaptiveCards/french/systemMessage.json");
const italianSystemMessageCard = require("./adaptiveCards/italian/systemMessage.json");
const germanSystemMessageClosingCard = require("./adaptiveCards/german/systemMessageClosing.json");
const englishSystemMessageClosingCard = require("./adaptiveCards/english/systemMessageClosing.json");
const frenchSystemMessageClosingCard = require("./adaptiveCards/french/systemMessageClosing.json");
const italianSystemMessageClosingCard = require("./adaptiveCards/italian/systemMessageClosing.json");
const germanSystemMessageSummarizingCard = require("./adaptiveCards/german/systemMessageSummarizing.json");
const englishSystemMessageSummarizingCard = require("./adaptiveCards/english/systemMessageSummarizing.json");
const frenchSystemMessageSummarizingCard = require("./adaptiveCards/french/systemMessageSummarizing.json");
const italianSystemMessageSummarizingCard = require("./adaptiveCards/italian/systemMessageSummarizing.json");
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
          resolve({
            answer: data.answer,
            autoClosedConversation: data.autoClosedConversation
          });
        }
      };
      this.htBotApi.getAnswer(prompt, userId, language, callback);
    });
  }

  async rateConversationNegative(userId) {
    return new Promise((resolve, reject) => {
      var callback = function (error, data, response) {
        if (error) {
          reject(error);
        } else {
          resolve(data.incidentReport);
        }
      };
      this.htBotApi.rateConversation(userId, false, callback);
    });
  }

  async rateConversationPositive(userId) {
    return new Promise((resolve, reject) => {
      var callback = function (error, data, response) {
        if (error) {
          reject(error);
        } else {
          resolve(data.resultCode);
        }
      };
      this.htBotApi.rateConversation(userId, true, callback);
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
  retrieveSystemMessageClosingCard(userLocale) {
    if(!userLocale) {
      return englishSystemMessageClosingCard;
    }

    var systemMessageCard;

    if(userLocale.startsWith("en-")) {
      systemMessageCard = englishSystemMessageClosingCard;
    } else if(userLocale.startsWith("de-")) {
      systemMessageCard = germanSystemMessageClosingCard;
    } else if(userLocale.startsWith("it-")) {
      systemMessageCard = italianSystemMessageClosingCard;
    } else if(userLocale.startsWith("fr-")) {
      systemMessageCard = frenchSystemMessageClosingCard;
    }

    return systemMessageCard;
  }

  retrieveSystemMessageSummarizingCard(userLocale) {
    if(!userLocale) {
      return englishSystemMessageSummarizingCard;
    }

    var systemMessageCard;

    if(userLocale.startsWith("en-")) {
      systemMessageCard = englishSystemMessageSummarizingCard;
    } else if(userLocale.startsWith("de-")) {
      systemMessageCard = germanSystemMessageSummarizingCard;
    } else if(userLocale.startsWith("it-")) {
      systemMessageCard = italianSystemMessageSummarizingCard;
    } else if(userLocale.startsWith("fr-")) {
      systemMessageCard = frenchSystemMessageSummarizingCard;
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
      try {
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
    } catch (error) {
        console.error(error);
        await context.sendActivity("There was an error while adding a member to the Bot. Please contact your administrator.");
        await next();
      }
    });


    this.onMessage(async (context, next) => {
      try {
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


        if (await this.hasOpenConversation(userId)) {
          await context.sendActivity(this.retrieveLanguageConfig(language).openConversation);
        } else {
          var botAnswer = await this.getAnswer(prompt, userId, this.mapToLanguageEnum(language));
          
          if(botAnswer.autoClosedConversation) {
            var closingObject = {answer: botAnswer.answer,
                            question: prompt}
            var systemMessageClosingCard = this.retrieveSystemMessageClosingCard(language);
            const card = cardTools.AdaptiveCards.declare(systemMessageClosingCard).render(closingObject);
            await context.sendActivity({ attachments: [CardFactory.adaptiveCard(card)] });
          } else {
            var answerobj = this.copyJSONObject(defaultSystemMessageProperties);
            answerobj.answer = botAnswer.answer;
            var systemMessageCard = this.retrieveSystemMessageCard(language);
            const card = cardTools.AdaptiveCards.declare(systemMessageCard).render(answerobj);
            await context.sendActivity({ attachments: [CardFactory.adaptiveCard(card)] });
          }
          return;
        }
      await next();
    } catch (error) {
      console.error(error);
      await context.sendActivity("There was an error while communicating with the Bot. Please contact your administrator.");
      await next();
    }
    });
  }

  // Invoked when an action is taken on an Adaptive Card. The Adaptive Card sends an event to the Bot and this
  // method handles that event.
  async onAdaptiveCardInvoke(context, invokeValue) {
    try{

    var locale = context.activity.locale;
    var userId = context.activity.from.id;
    var systemMessageCard = this.retrieveSystemMessageCard(locale);
    var answerObj = this.copyJSONObject(defaultSystemMessageProperties);
    var message = null;

    if (invokeValue.action.verb === "continueConversation") {
    var reply = await this.continueConversation(userId);

    answerObj.answer = invokeValue.action.id;
    answerObj.continueConversationTextVisible = true;
    answerObj.buttonsVisible = false;

    message = this.retrieveLanguageConfig(context.activity.locale).continueConversation;
    await context.sendActivity(message);
    } else if (invokeValue.action.verb === "negativeRating") {
      var incidentReport = await this.rateConversationNegative(context.activity.from.id, false);
      systemMessageCard = this.retrieveSystemMessageCard(context.activity.locale);

      answerObj.answer = invokeValue.action.id;
      answerObj.negativeRatingVisible = true;
      answerObj.buttonsVisible = false;

      var summarizingObject = {report: incidentReport}
      var systemMessageSummarizingCard = this.retrieveSystemMessageSummarizingCard(locale);
      const summarizingCard = cardTools.AdaptiveCards.declare(systemMessageSummarizingCard).render(summarizingObject);
      await context.sendActivity({ attachments: [CardFactory.adaptiveCard(summarizingCard)] });
    } else if (invokeValue.action.verb === "positiveRating") {
      var reply = await this.rateConversationPositive(context.activity.from.id, true);

      answerObj.answer = invokeValue.action.id;
      answerObj.positiveRatingVisible = true;
      answerObj.buttonsVisible = false;

      message = this.retrieveLanguageConfig(context.activity.locale).positiveRating;
      await context.sendActivity(message);
    }

    const card = cardTools.AdaptiveCards.declare(systemMessageCard).render(answerObj);
    await context.updateActivity({
      type: "message",
      id: context.activity.replyToId,
      attachments: [CardFactory.adaptiveCard(card)],
    });
    return { statusCode: 200 };
  } catch (error) {
    console.error(error);
    await context.sendActivity("There was an error while interacting with one of the cards. Please contact your administrator.");
  }
  }
}

module.exports.TeamsBot = TeamsBot;
