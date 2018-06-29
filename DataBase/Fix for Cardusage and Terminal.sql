ALTER TABLE Terminal DROP CONSTRAINT Terminal_CardUsage_FK;
ALTER TABLE Terminal DROP COLUMN CardUsage_useID;
ALTER TABLE CardUsage ADD COLUMN Terminal_terminalID INTEGER NOT NULL;
ALTER TABLE CardUsage ADD CONSTRAINT CardUsage_Terminal_FK FOREIGN KEY ( Terminal_terminalID ) REFERENCES Terminal ( terminalID ) ;
