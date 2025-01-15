import React, {useEffect, useRef, useState} from 'react';
import { Container, Box, Button, TextField, Typography, Paper } from '@mui/material';
import {Configuration, DefaultApi} from './api-client';
import Backdrop from "@mui/material/Backdrop";
import CircularProgress from "@mui/material/CircularProgress";

const App: React.FC = () => {

  const [conversationStarted, setConversationStarted] = useState(false);
  const [messages, setMessages] = useState<string[]>([]);
  const [userInput, setUserInput] = useState('');
  const [userId, setUserId] = useState('');
  const [apiToken, setApiToken] = useState('');
  const [apiInitialized, setApiInitialized] = useState(false);
  const [api, setApi] = useState<DefaultApi>();
  const [loading, setLoading] = useState(false);

  const initializeApi = () => {
    const config = new Configuration({ basePath: 'http://172.201.109.248:8080/hti-bot-backend-1.0.0-SNAPSHOT/hti-bot-backend-1.0.0/rest/v1.0', baseOptions: {
        headers: {
          'X-API-Key': apiToken,
        },
      },});
    setApi(new DefaultApi(config));
    setApiInitialized(true);
  };

  const getBotResponse = async (input: string): Promise<string> => {
    try {
      setLoading(true)

      const hasOpenConversation = await api?.hasOpenConversation(userId);

      if(hasOpenConversation) {
        await api?.rateConversation(userId, true);
      }

      const answerReponse = await api?.getAnswer(input, userId);

      const hasOpenConversationAfter = await api?.hasOpenConversation(userId);

      if(!hasOpenConversationAfter) {
        await api?.continueConversation(userId)
      }

      return ""+answerReponse?.data.answer;
    } finally {
      setLoading(false);
    }
  };


  const startConversation = async () => {
    setConversationStarted(true);
    setMessages(["Hello! I'm Leisure Time Bot, your guide for regulatory information regarding leisure time activities in Tyrol, Austria." +
    "Please provide me a question and I will try my best to answer." +
    "If you want to chat about another topic, close the conversation and start a new one please! ❤️"]);
  };

  const endConversation = async () => {
    setConversationStarted(false);

    const hasOpenConversation = await api?.hasOpenConversation(userId);

    if(hasOpenConversation) {
      await api?.rateConversation(userId, true);
    }
    setMessages([]);
    setUserInput('');
  };


  const handleUserInputSubmit = async () => {
    if (!userInput.trim()) return;
    const botResponse = await getBotResponse(userInput);
    setMessages((prevMessages) => [...prevMessages, `User: ${userInput}`, `Bot: ${botResponse}`]);
    setUserInput('');
  };

  return (
      <Container maxWidth="sm" sx={{ mt: 5 }}>
        <Paper elevation={3} sx={{ p: 3 }}>
          <Typography variant="h4" gutterBottom align="center">
            Leisure Time Bot
          </Typography>

          {apiInitialized ? (
              <>
                {!conversationStarted ? (
                    <Box display="flex" justifyContent="center" mt={3}>
                      <Button variant="contained" color="primary" onClick={startConversation}>
                        Start Conversation
                      </Button>
                    </Box>
                ) : (
                    <>
                      <Box
                          sx={{
                            border: '1px solid #ccc',
                            borderRadius: 2,
                            p: 2,
                            height: '300px',
                            overflowY: 'auto',
                            mb: 2,
                          }}
                      >
                        {messages.map((message, index) => (
                            <Typography key={index} sx={{ mb: 1 }}>
                              {message}
                            </Typography>
                        ))}
                      </Box>

                      <Box display="flex" gap={1}>
                        <TextField
                            fullWidth
                            variant="outlined"
                            placeholder="Type your message..."
                            value={userInput}
                            onChange={(e) => setUserInput(e.target.value)}
                            onKeyPress={(e) => {
                              if (e.key === 'Enter') handleUserInputSubmit();
                            }}
                        />
                        <Button variant="contained" color="primary" onClick={handleUserInputSubmit}>
                          Send
                        </Button>
                      </Box>

                      <Box display="flex" justifyContent="center" mt={3}>
                        {messages.some((message) => message.includes('User:')) && (
                            <Button variant="outlined" color="secondary" onClick={endConversation}>
                              End Conversation
                            </Button>
                        )}
                      </Box>
                    </>
                )}
              </>
          ) : (
              <Box>
                <Typography variant="h6" gutterBottom>
                  Please provide your User ID and API Token
                </Typography>
                <TextField
                    label="User ID"
                    fullWidth
                    value={userId}
                    onChange={(e) => setUserId(e.target.value)}
                    sx={{ mb: 2 }}
                />
                <TextField
                    label="API Token"
                    fullWidth
                    value={apiToken}
                    onChange={(e) => setApiToken(e.target.value)}
                    sx={{ mb: 2 }}
                />
                <Box display="flex" justifyContent="center" mt={3}>
                  <Button
                      variant="contained"
                      color="primary"
                      onClick={() => {
                        if (userId && apiToken) {
                          initializeApi();
                        }
                      }}
                  >
                    Submit
                  </Button>
                </Box>
              </Box>
          )}
        </Paper>
        <Backdrop
            sx={{ color: "#fff", zIndex: (theme) => theme.zIndex.drawer + 1 }}
            open={loading}
        >
          <CircularProgress color="inherit" />
          <span style={{ marginLeft: "10px" }}>Getting answer...</span>
        </Backdrop>
      </Container>
  );
};

export default App;
