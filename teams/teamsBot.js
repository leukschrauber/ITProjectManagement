const { TeamsActivityHandler, CardFactory, TurnContext } = require("botbuilder");
const rawWelcomeCard = require("./adaptiveCards/welcome.json");
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


    this.onMessage(async (context, next) => {

      var userId = context.activity.from.id;
      try {
        if (await hasOpenConversation(userId)) {
          context.sendActivity("User has open conversation");
        } else {
          context.sendActivity("User has no open conversation");
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

      // Trigger command by IM text
      switch (txt) {
        case "welcome": {
          const card = cardTools.AdaptiveCards.declareWithoutData(rawWelcomeCard).render();
          await context.sendActivity({ attachments: [CardFactory.adaptiveCard(card)] });
          break;
        }
        case "learn": {
          this.likeCountObj.likeCount = 0;
          const card = cardTools.AdaptiveCards.declare(rawLearnCard).render(this.likeCountObj);
          await context.sendActivity({ attachments: [CardFactory.adaptiveCard(card)] });
          break;
        }
        /**
         * case "yourCommand": {
         *   await context.sendActivity(`Add your response here!`);
         *   break;
         * }
         */
      }

      // By calling next() you ensure that the next BotHandler is run.
      await next();
    });

    // Listen to MembersAdded event, view https://docs.microsoft.com/en-us/microsoftteams/platform/resources/bot-v3/bots-notifications for more events
    this.onMembersAdded(async (context, next) => {
      const membersAdded = context.activity.membersAdded;
      for (let cnt = 0; cnt < membersAdded.length; cnt++) {
        if (membersAdded[cnt].id) {
          const card = cardTools.AdaptiveCards.declareWithoutData(rawWelcomeCard).render();
          await context.sendActivity({ attachments: [CardFactory.adaptiveCard(card)] });
          break;
        }
      }
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
