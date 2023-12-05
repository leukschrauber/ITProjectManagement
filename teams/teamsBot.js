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

    var client = new ApiClient.ApiClient("http://localhost:9191/hti-bot-backend-1.0.0-SNAPSHOT/hti-bot-backend-1.0.0/rest/v1.0");
    client.defaultHeaders = {
        'X-API-Key': '9562015e-95c8-44bc-a5c3-d8f8c132b429'
    }
    this.htBotApi = new ApiClient.DefaultApi(client);

    //TODO: Move these methods out of the constructor

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
          //TODO: Translation
          context.sendActivity("It seems as there would be an open conversation for you. If you'd like to continue in this conversation, let us know by clicking the button. Otherwise, rate the conversation to close it.");
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
      /*
      Some pseudo code to show, what we will be doing here:

      onMembersAdded():
      translate(hard-coded welcome message, context.activity.text)
      Define welcome message card in adaptiveCards

      onMessage():

        if(userHasUnfinishedConversation(context.activity.from.id)):
          send message: translate(please rate our conversation or choose to continue the conversation, context.activity.locale)

        else:
          var answer = conversate(message, context.activity.from.id, context.activity.locale)
          var pictures = answer.pictures
          var text = answer.text
          await context.sendActivity({ attachments: [CardFactory.adaptiveCard(card, pictures, text)] });
          
      onAdaptiveCardInvoke():
        if invoke.value.verb === "user-like":
           finishConversation(succesful: true, context.activity.from.id);
           send translate(hard-coded thank you note, context.activity.locale)
        if invoke.value.verb === "user-dislike":
          var incidentReport = finishConversation(succesful: false, context.activity.from.id)
          await context.sendActivity({ attachments: [CardFactory.adaptiveCard(card, incidentReport)] });
        if invoke.value.verb === "continue-conversation":
          continueConversation(context.activitity.from.id)
          send translate(hard-coded message: please continue)
      */
      await next();
    });
  }

  // Invoked when an action is taken on an Adaptive Card. The Adaptive Card sends an event to the Bot and this
  // method handles that event.
  //TODO: Make this work
  async onAdaptiveCardInvoke(context, invokeValue) {
    // The verb "userlike" is sent from the Adaptive Card defined in adaptiveCards/learn.json
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
    await next();
  }
}

module.exports.TeamsBot = TeamsBot;
