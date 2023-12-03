const { TeamsActivityHandler, CardFactory, TurnContext, useBotState } = require("botbuilder");
const germanWelcomeCard = require("./adaptiveCards/german/welcome.json");
const englishWelcomeCard = require("./adaptiveCards/english/welcome.json");
const frenchWelcomeCard = require("./adaptiveCards/french/welcome.json");
const italianWelcomeCard = require("./adaptiveCards/italian/welcome.json");
const rawLearnCard = require("./adaptiveCards/learn.json");
const cardTools = require("@microsoft/adaptivecards-tools");
const ApiClient =  require("./apiclient");

class TeamsBot extends TeamsActivityHandler {
  constructor() {
    super();
    var client = new ApiClient.ApiClient("http://localhost:9191/hti-bot-backend-1.0.0-SNAPSHOT/hti-bot-backend-1.0.0/rest/v1.0");
    client.defaultHeaders = {
      'X-API-Key': '9562015e-95c8-44bc-a5c3-d8f8c132b429'
  }
    var htBotApi = new ApiClient.DefaultApi(client);

    // record the likeCount
    this.likeCountObj = { likeCount: 0 };

    async function hasOpenConversation(userId) {
      return new Promise((resolve, reject) => {
        var callback = function (error, data, response) {
          if (error) {
            reject(error);
          } else {
            resolve(data.hasOpenConversation);
          }
        };
        htBotApi.hasOpenConversation(userId, callback);
      });
    }

    async function getAnswer(prompt, userId, language) {
      return new Promise((resolve, reject) => {
        var callback = function (error, data, response) {
          if (error) {
            reject(error);
          } else {
            resolve(data.answer);
          }
        };
        htBotApi.getAnswer(prompt, userId, language, callback);
      });
    }

    function retrieveWelcomeCard(userLocale) {
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

    function mapToLanguageEnum(userLocale) {
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

    this.onMembersAdded(async (context, next) => {
      const membersAdded = context.activity.membersAdded;
      for (let cnt = 0; cnt < 1; cnt++) {
        if (membersAdded[cnt].id) {
        var welcomeCard = retrieveWelcomeCard(membersAdded.locale);

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
          var welcomeCard = retrieveWelcomeCard(language);
          const card = cardTools.AdaptiveCards.declareWithoutData(welcomeCard).render();
          await context.sendActivity({ attachments: [CardFactory.adaptiveCard(card)] });
          return;
        }
      }


      try {
        if (await hasOpenConversation(userId)) {
          //TODO: Translation
          context.sendActivity("It seems as there would be an open conversation for you. If you'd like to continue in this conversation, let us know by clicking the button. Otherwise, rate the conversation to close it.");
        } else {
          var answer = await getAnswer(prompt, userId, mapToLanguageEnum(language));
          //TODO: Adaptive card
          context.sendActivity(answer)
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
      console.log("Running with Message Activity.");
      let txt = context.activity.text;
      context.activity.from.id
      const removedMentionText = TurnContext.removeRecipientMention(context.activity);
      if (removedMentionText) {
        // Remove the line break
        txt = removedMentionText.toLowerCase().replace(/\n|\r/g, "").trim();
      }
      // By calling next() you ensure that the next BotHandler is run.
      await next();
    });
  }

  // Invoked when an action is taken on an Adaptive Card. The Adaptive Card sends an event to the Bot and this
  // method handles that event.
  async onAdaptiveCardInvoke(context, invokeValue) {
    // The verb "userlike" is sent from the Adaptive Card defined in adaptiveCards/learn.json
    if (invokeValue.action.verb === "userlike") {
      this.likeCountObj.likeCount++;
      const card = cardTools.AdaptiveCards.declare(rawLearnCard).render(this.likeCountObj);
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
